package com.location.identites.controller;

import com.location.identites.dto.TwoFactorCodeRequest;
import com.location.identites.dto.TwoFactorSetupResponse;
import com.location.identites.service.AuthService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/2fa")
public class SecurityController {

    private final AuthService authService;

    public SecurityController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/setup")
    public TwoFactorSetupResponse setup(Authentication authentication) {
        return authService.setup2fa(UUID.fromString(authentication.getName()));
    }

    @PostMapping("/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enable(Authentication authentication, @Valid @RequestBody TwoFactorCodeRequest request) {
        authService.enable2fa(UUID.fromString(authentication.getName()), request.code());
    }

    @PostMapping("/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disable(Authentication authentication, @Valid @RequestBody TwoFactorCodeRequest request) {
        authService.disable2fa(UUID.fromString(authentication.getName()), request.code());
    }
}
