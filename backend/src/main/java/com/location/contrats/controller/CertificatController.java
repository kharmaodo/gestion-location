package com.location.contrats.controller;

import com.location.contrats.dto.CertificatResponse;
import com.location.contrats.service.CertificatService;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CertificatController {
    private final CertificatService service;

    public CertificatController(CertificatService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/contrats/{id}/certificat")
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public CertificatResponse get(Authentication auth, @PathVariable UUID id) {
        return service.emettre(UUID.fromString(auth.getName()), id);
    }
}
