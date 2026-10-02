package com.location.identites;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.location.identites.dto.LoginRequest;
import com.location.identites.dto.RegisterRequest;
import com.location.identites.entity.UtilisateurEntity;
import com.location.identites.repository.ConsentementRepository;
import com.location.identites.repository.RefreshTokenRepository;
import com.location.identites.repository.ResetPasswordRepository;
import com.location.identites.repository.UtilisateurRepository;
import com.location.identites.repository.UtilisateurRoleRepository;
import com.location.identites.service.AuthService;
import com.location.identites.service.MailService;
import com.location.shared.exception.ApiException;
import com.location.shared.security.JwtService;
import com.location.shared.security.TotpService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock UtilisateurRepository utilisateurs;
    @Mock UtilisateurRoleRepository roles;
    @Mock ConsentementRepository consentements;
    @Mock RefreshTokenRepository refreshTokens;
    @Mock ResetPasswordRepository resetTokens;
    @Mock PasswordEncoder encoder;
    @Mock JwtService jwtService;
    @Mock TotpService totpService;
    @Mock MailService mailService;

    AuthService service;

    @BeforeEach
    void setUp() {
        service = new AuthService(utilisateurs, roles, consentements, refreshTokens, resetTokens, encoder, jwtService, totpService, mailService);
    }

    @Test
    void refuseUnTypeDeCompteInconnu() {
        RegisterRequest request = new RegisterRequest("ADMIN", "a@test.sn", null, "Motdepasse1", "Awa", "Fall", true);
        ApiException ex = assertThrows(ApiException.class, () -> service.register(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void refuseUnMotDePasseFaux() {
        UtilisateurEntity user = new UtilisateurEntity();
        user.setMotDePasseHash("hash");
        user.setStatut("ACTIF");
        when(utilisateurs.findByEmail("a@test.sn")).thenReturn(Optional.of(user));
        when(encoder.matches("mauvais", "hash")).thenReturn(false);
        ApiException ex = assertThrows(ApiException.class, () -> service.login(new LoginRequest("a@test.sn", "mauvais", null)));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
    }
}
