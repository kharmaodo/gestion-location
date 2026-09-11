package com.location.contrats.controller;

import com.location.contrats.dto.EtatLieuxRequest;
import com.location.contrats.dto.EtatLieuxResponse;
import com.location.contrats.service.EtatLieuxService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/etats-lieux")
@PreAuthorize("hasRole('PROPRIETAIRE')")
public class EtatLieuxController {
    private final EtatLieuxService service;

    public EtatLieuxController(EtatLieuxService service) {
        this.service = service;
    }

    @GetMapping
    public List<EtatLieuxResponse> lister(Authentication auth, @RequestParam UUID contratId) {
        return service.lister(UUID.fromString(auth.getName()), contratId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EtatLieuxResponse creer(Authentication auth, @Valid @RequestBody EtatLieuxRequest request) {
        return service.creer(UUID.fromString(auth.getName()), request);
    }

    @PostMapping("/{id}/validation")
    public EtatLieuxResponse valider(Authentication auth, @PathVariable UUID id) {
        return service.valider(UUID.fromString(auth.getName()), id);
    }
}
