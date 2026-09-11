package com.location.paiements.controller;

import com.location.paiements.dto.EcheanceResponse;
import com.location.paiements.dto.IntentionRequest;
import com.location.paiements.dto.IntentionResponse;
import com.location.paiements.dto.PaiementRequest;
import com.location.paiements.dto.RelanceResponse;
import com.location.paiements.service.PaiementEnLigneService;
import com.location.paiements.service.PaiementService;
import com.location.paiements.service.RelanceService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/loyers")
@PreAuthorize("hasRole('PROPRIETAIRE')")
public class PaiementController {
    private final PaiementService service;
    private final RelanceService relances;
    private final PaiementEnLigneService enLigne;

    public PaiementController(PaiementService service, RelanceService relances, PaiementEnLigneService enLigne) {
        this.service = service;
        this.relances = relances;
        this.enLigne = enLigne;
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

    @PostMapping("/{id}/paiement-en-ligne")
    @ResponseStatus(HttpStatus.CREATED)
    public IntentionResponse initier(
            Authentication auth, @PathVariable UUID id, @Valid @RequestBody IntentionRequest request) {
        return enLigne.initier(UUID.fromString(auth.getName()), id, request.fournisseur());
    }

    @PostMapping("/relances")
    public List<RelanceResponse> relancer(Authentication auth) {
        return relances.declencher(UUID.fromString(auth.getName()));
    }

    @GetMapping("/relances")
    public List<RelanceResponse> listerRelances(Authentication auth) {
        return relances.lister(UUID.fromString(auth.getName()));
    }
}
