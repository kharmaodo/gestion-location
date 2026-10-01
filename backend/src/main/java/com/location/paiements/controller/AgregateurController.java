package com.location.paiements.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/psp")
@PreAuthorize("isAuthenticated()")
public class AgregateurController {
    private final String provider;

    public AgregateurController(@Value("${app.psp.provider:MOCK}") String provider) {
        this.provider = provider;
    }

    @GetMapping
    public Map<String, String> statut() {
        boolean mock = "MOCK".equalsIgnoreCase(provider);
        return Map.of(
                "provider", provider.toUpperCase(),
                "mode", mock ? "SANDBOX" : "LIVE",
                "detail", mock ? "Webhook mock. Definir app.psp.provider=WAVE et les cles pour le live." : "Provider configure");
    }
}
