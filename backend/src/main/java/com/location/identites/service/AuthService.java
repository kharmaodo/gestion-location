package com.location.identites.service;

import com.location.identites.dto.LoginRequest;
import com.location.identites.dto.MeResponse;
import com.location.identites.dto.RegisterRequest;
import com.location.identites.dto.TokenResponse;
import com.location.identites.dto.TwoFactorSetupResponse;
import com.location.identites.entity.ConsentementEntity;
import com.location.identites.entity.RefreshTokenEntity;
import com.location.identites.entity.ResetPasswordEntity;
import com.location.identites.entity.UtilisateurEntity;
import com.location.identites.entity.UtilisateurRoleEntity;
import com.location.identites.repository.ConsentementRepository;
import com.location.identites.repository.RefreshTokenRepository;
import com.location.identites.repository.ResetPasswordRepository;
import com.location.identites.repository.UtilisateurRepository;
import com.location.identites.repository.UtilisateurRoleRepository;
import com.location.shared.exception.ApiException;
import com.location.shared.security.JwtService;
import com.location.shared.security.TotpService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurs;
    private final UtilisateurRoleRepository roles;
    private final ConsentementRepository consentements;
    private final RefreshTokenRepository refreshTokens;
    private final ResetPasswordRepository resetTokens;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final TotpService totpService;
    private final MailService mailService;

    public AuthService(
            UtilisateurRepository utilisateurs,
            UtilisateurRoleRepository roles,
            ConsentementRepository consentements,
            RefreshTokenRepository refreshTokens,
            ResetPasswordRepository resetTokens,
            PasswordEncoder encoder,
            JwtService jwtService,
            TotpService totpService,
            MailService mailService) {
        this.utilisateurs = utilisateurs;
        this.roles = roles;
        this.consentements = consentements;
        this.refreshTokens = refreshTokens;
        this.resetTokens = resetTokens;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.totpService = totpService;
        this.mailService = mailService;
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        String type = request.typeCompte().toUpperCase(Locale.ROOT);
        if (!List.of("PROPRIETAIRE", "LOCATAIRE").contains(type)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "typeCompte invalide");
        }
        if ((request.email() == null || request.email().isBlank())
                && (request.telephone() == null || request.telephone().isBlank())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "email ou telephone requis");
        }
        if (request.email() != null && utilisateurs.findByEmail(request.email()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "Un compte existe deja avec cet email");
        }
        if (request.telephone() != null && !request.telephone().isBlank()
                && utilisateurs.findByTelephone(request.telephone()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "Un compte existe deja avec ce telephone");
        }
        UtilisateurEntity user = new UtilisateurEntity();
        user.setId(UUID.randomUUID());
        user.setEmail(blankToNull(request.email()));
        user.setTelephone(blankToNull(request.telephone()));
        user.setMotDePasseHash(encoder.encode(request.motDePasse()));
        user.setPrenom(request.prenom());
        user.setNom(request.nom());
        utilisateurs.save(user);
        UtilisateurRoleEntity role = new UtilisateurRoleEntity();
        role.setId(UUID.randomUUID());
        role.setUtilisateurId(user.getId());
        role.setRole(type);
        roles.save(role);
        ConsentementEntity consent = new ConsentementEntity();
        consent.setId(UUID.randomUUID());
        consent.setUtilisateurId(user.getId());
        consent.setType("RGPD_INSCRIPTION");
        consent.setSource("INSCRIPTION");
        consent.setAccepte(request.consentementRgpd());
        consentements.save(consent);
        return issueTokens(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        UtilisateurEntity user = findByIdentifiant(request.identifiant());
        if (!encoder.matches(request.motDePasse(), user.getMotDePasseHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
        }
        if (!"ACTIF".equals(user.getStatut())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Compte inactif");
        }
        if (user.isTwoFactorActive()) {
            if (request.otp() == null || request.otp().isBlank()) {
                return TokenResponse.pending2fa(jwtService.createPending2faToken(user.getId()));
            }
            if (!totpService.verify(user.getTwoFactorSecret(), request.otp())) {
                throw new ApiException(HttpStatus.UNAUTHORIZED, "Code 2FA invalide");
            }
        }
        return issueTokens(user);
    }

    @Transactional
    public TokenResponse verifyLogin2fa(String pendingToken, String code) {
        UUID userId;
        try {
            userId = jwtService.requirePending2fa(pendingToken);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Session 2FA expiree");
        }
        UtilisateurEntity user = utilisateurs.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"));
        if (!totpService.verify(user.getTwoFactorSecret(), code)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Code 2FA invalide");
        }
        return issueTokens(user);
    }

    @Transactional
    public TokenResponse refresh(String refreshToken) {
        RefreshTokenEntity stored = refreshTokens
                .findByJtiHashAndRevoqueFalse(sha256(refreshToken))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token invalide"));
        if (stored.getExpireLe().isBefore(Instant.now())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token expire");
        }
        stored.setRevoque(true);
        refreshTokens.save(stored);
        UtilisateurEntity user = utilisateurs.findById(stored.getUtilisateurId())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"));
        return issueTokens(user);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokens.findByJtiHashAndRevoqueFalse(sha256(refreshToken)).ifPresent(token -> {
            token.setRevoque(true);
            refreshTokens.save(token);
        });
    }

    @Transactional(readOnly = true)
    public MeResponse me(UUID userId) {
        UtilisateurEntity user = utilisateurs.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        List<String> roleNames = roles.findByUtilisateurId(user.getId()).stream()
                .map(UtilisateurRoleEntity::getRole).toList();
        return new MeResponse(user.getId(), user.getEmail(), user.getTelephone(), user.getPrenom(), user.getNom(), roleNames);
    }

    @Transactional
    public void forgotPassword(String identifiant) {
        utilisateurs.findByEmail(identifiant).or(() -> utilisateurs.findByTelephone(identifiant)).ifPresent(user -> {
            String raw = UUID.randomUUID() + "." + UUID.randomUUID();
            ResetPasswordEntity token = new ResetPasswordEntity();
            token.setId(UUID.randomUUID());
            token.setUtilisateurId(user.getId());
            token.setTokenHash(sha256(raw));
            token.setExpireLe(Instant.now().plusSeconds(3600));
            token.setUtilise(false);
            resetTokens.save(token);
            mailService.sendResetPassword(user.getEmail() != null ? user.getEmail() : identifiant, raw);
        });
    }

    @Transactional
    public void resetPassword(String rawToken, String nouveau) {
        ResetPasswordEntity token = resetTokens.findByTokenHashAndUtiliseFalse(sha256(rawToken))
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Lien invalide ou expire"));
        if (token.getExpireLe().isBefore(Instant.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Lien invalide ou expire");
        }
        UtilisateurEntity user = utilisateurs.findById(token.getUtilisateurId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Lien invalide ou expire"));
        user.setMotDePasseHash(encoder.encode(nouveau));
        utilisateurs.save(user);
        token.setUtilise(true);
        resetTokens.save(token);
    }

    @Transactional
    public TwoFactorSetupResponse setup2fa(UUID userId) {
        UtilisateurEntity user = utilisateurs.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        String secret = totpService.generateSecret();
        user.setTwoFactorSecret(secret);
        user.setTwoFactorActive(false);
        utilisateurs.save(user);
        String account = user.getEmail() != null ? user.getEmail() : user.getTelephone();
        return new TwoFactorSetupResponse(secret, totpService.otpauthUrl("GestionLocation", account, secret));
    }

    @Transactional
    public void enable2fa(UUID userId, String code) {
        UtilisateurEntity user = utilisateurs.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        if (user.getTwoFactorSecret() == null || !totpService.verify(user.getTwoFactorSecret(), code)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Code 2FA invalide");
        }
        user.setTwoFactorActive(true);
        utilisateurs.save(user);
    }

    @Transactional
    public void disable2fa(UUID userId, String code) {
        UtilisateurEntity user = utilisateurs.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        if (!totpService.verify(user.getTwoFactorSecret(), code)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Code 2FA invalide");
        }
        user.setTwoFactorActive(false);
        user.setTwoFactorSecret(null);
        utilisateurs.save(user);
    }

    private TokenResponse issueTokens(UtilisateurEntity user) {
        List<String> roleNames = roles.findByUtilisateurId(user.getId()).stream()
                .map(UtilisateurRoleEntity::getRole).toList();
        String subject = user.getEmail() != null ? user.getEmail() : user.getTelephone();
        String access = jwtService.createAccessToken(user.getId(), subject, roleNames);
        String refreshRaw = UUID.randomUUID() + "." + UUID.randomUUID();
        RefreshTokenEntity refresh = new RefreshTokenEntity();
        refresh.setId(UUID.randomUUID());
        refresh.setUtilisateurId(user.getId());
        refresh.setJtiHash(sha256(refreshRaw));
        refresh.setExpireLe(jwtService.refreshExpiry());
        refresh.setRevoque(false);
        refreshTokens.save(refresh);
        return TokenResponse.tokens(access, refreshRaw, jwtService.accessExpiry(), user.getId(), roleNames);
    }

    private UtilisateurEntity findByIdentifiant(String identifiant) {
        return utilisateurs.findByEmail(identifiant)
                .or(() -> utilisateurs.findByTelephone(identifiant))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Identifiants invalides"));
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
