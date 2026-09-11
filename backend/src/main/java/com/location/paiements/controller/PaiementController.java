package com.location.paiements.controller;

import com.location.paiements.dto.EcheanceResponse;
import com.location.paiements.dto.PaiementRequest;
import com.location.paiements.service.PaiementService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/loyers")
@PreAuthorize("hasRole('PROPRIETAIRE')")
public class PaiementController {
    private final PaiementService service;

    public PaiementController(PaiementService service) {
        this.service = service;
    }

    @GetMapping
    public List<EcheanceResponse> lister(Authentication auth) {
        return service.lister(UUID.fromString(auth.getName()));
    }

    @PostMapping("/generation")
    public List<EcheanceResponse> generer(Authentication auth) {
        return service.generer(UUID.fromString(auth.getName()));
    }

    @PostMapping("/{id}/paiements")
    public EcheanceResponse encaisser(
            Authentication auth, @PathVariable UUID id, @Valid @RequestBody PaiementRequest request) {
        return service.encaisser(UUID.fromString(auth.getName()), id, request);
    }
}
