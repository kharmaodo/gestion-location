package com.location.identites.controller;

import com.location.identites.dto.MeResponse;
import com.location.identites.service.AuthService;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ProfilController {

    private final AuthService authService;

    public ProfilController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public MeResponse me(Authentication authentication) {
        return authService.me(UUID.fromString(authentication.getName()));
    }
}
