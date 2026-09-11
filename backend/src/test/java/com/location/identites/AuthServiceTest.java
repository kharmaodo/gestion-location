package com.location.identites;

import static org.assertj.core.api.Assertions.assertThat;
import com.location.identites.dto.LoginRequest;
import com.location.identites.dto.RegisterRequest;
import com.location.identites.dto.TokenResponse;
import com.location.identites.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {
    @Autowired AuthService authService;

    @Test
    void registerThenLogin() {
        TokenResponse created = authService.register(new RegisterRequest(
                "PROPRIETAIRE", "owner2@test.sn", null, "Motdepasse1", "Awa", "Fall", true));
        assertThat(created.accessToken()).isNotBlank();
        assertThat(created.requiresTwoFactor()).isFalse();
        TokenResponse login = authService.login(new LoginRequest("owner2@test.sn", "Motdepasse1", null));
        assertThat(login.userId()).isEqualTo(created.userId());
    }
}
