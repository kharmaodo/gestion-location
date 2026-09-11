package com.location.contrats.controller;

import com.location.contrats.dto.AvenantRequest;
import com.location.contrats.dto.ContratRequest;
import com.location.contrats.dto.ContratResponse;
import com.location.contrats.service.ContratService;
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
@RequestMapping("/api/v1/contrats")
@PreAuthorize("hasRole('PROPRIETAIRE')")
public class ContratController {
    private final ContratService service;

    public ContratController(ContratService service) {
        this.service = service;
    }

    @GetMapping
    public List<ContratResponse> lister(Authentication auth) {
        return service.lister(UUID.fromString(auth.getName()));
    }

    @GetMapping("/{id}")
    public ContratResponse detail(Authentication auth, @PathVariable UUID id) {
        return service.detail(UUID.fromString(auth.getName()), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContratResponse creer(Authentication auth, @Valid @RequestBody ContratRequest request) {
        return service.creer(UUID.fromString(auth.getName()), request);
    }

    @PostMapping("/{id}/activation")
    public ContratResponse activer(Authentication auth, @PathVariable UUID id) {
        return service.activer(UUID.fromString(auth.getName()), id);
    }

    @PostMapping("/{id}/resiliation")
    public ContratResponse resilier(Authentication auth, @PathVariable UUID id) {
        return service.resilier(UUID.fromString(auth.getName()), id);
    }

    @PostMapping("/{id}/avenants")
    @ResponseStatus(HttpStatus.CREATED)
    public ContratResponse avenant(
            Authentication auth, @PathVariable UUID id, @Valid @RequestBody AvenantRequest request) {
        return service.avenant(UUID.fromString(auth.getName()), id, request);
    }
}
