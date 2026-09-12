package com.location.contrats.controller;

import com.location.contrats.dto.RestitutionCautionResponse;
import com.location.contrats.service.RestitutionCautionService;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestitutionCautionController {
    private final RestitutionCautionService service;

    public RestitutionCautionController(RestitutionCautionService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/contrats/{id}/restitution-caution")
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public RestitutionCautionResponse get(Authentication auth, @PathVariable UUID id) {
        return service.calculer(UUID.fromString(auth.getName()), id);
    }
}
