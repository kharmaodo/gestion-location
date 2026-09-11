package com.location.identites.controller;

import com.location.identites.dto.ForgotPasswordRequest;
import com.location.identites.dto.LoginRequest;
import com.location.identites.dto.RefreshRequest;
import com.location.identites.dto.RegisterRequest;
import com.location.identites.dto.ResetPasswordRequest;
import com.location.identites.dto.TokenResponse;
import com.location.identites.dto.TwoFactorCodeRequest;
import com.location.identites.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/2fa/verify")
    public TokenResponse verify2fa(
            @RequestHeader("X-2FA-Pending") String pendingToken,
            @Valid @RequestBody TwoFactorCodeRequest request) {
        return authService.verifyLogin2fa(pendingToken, request.code());
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request.refreshToken());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshRequest request) {
        authService.logout(request.refreshToken());
    }

    @PostMapping("/password/forgot")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void forgot(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.identifiant());
    }

    @PostMapping("/password/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reset(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.token(), request.nouveauMotDePasse());
    }
}
