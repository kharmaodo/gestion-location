package com.location.identites.service;

import com.location.identites.dto.LoginRequest;
import com.location.identites.dto.MeResponse;
import com.location.identites.dto.RegisterRequest;
import com.location.identites.dto.TokenResponse;
import com.location.identites.entity.ConsentementEntity;
import com.location.identites.entity.RefreshTokenEntity;
import com.location.identites.entity.UtilisateurEntity;
import com.location.identites.entity.UtilisateurRoleEntity;
import com.location.identites.repository.ConsentementRepository;
import com.location.identites.repository.RefreshTokenRepository;
import com.location.identites.repository.UtilisateurRepository;
import com.location.identites.repository.UtilisateurRoleRepository;
import com.location.shared.exception.ApiException;
import com.location.shared.security.JwtService;
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
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthService(
            UtilisateurRepository utilisateurs,
            UtilisateurRoleRepository roles,
            ConsentementRepository consentements,
            RefreshTokenRepository refreshTokens,
            PasswordEncoder encoder,
            JwtService jwtService) {
        this.utilisateurs = utilisateurs;
        this.roles = roles;
        this.consentements = consentements;
        this.refreshTokens = refreshTokens;
        this.encoder = encoder;
        this.jwtService = jwtService;
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
        return new TokenResponse(access, refreshRaw, jwtService.accessExpiry(), user.getId(), roleNames);
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
