package com.location.biens.controller;

import com.location.biens.dto.BienRequest;
import com.location.biens.dto.BienResponse;
import com.location.biens.dto.PeriodiciteRequest;
import com.location.biens.dto.PublicationRequest;
import com.location.biens.dto.UniteRequest;
import com.location.biens.dto.UniteResponse;
import com.location.biens.service.BienService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/biens")
@PreAuthorize("hasRole('PROPRIETAIRE')")
public class BienController {

    private final BienService bienService;

    public BienController(BienService bienService) {
        this.bienService = bienService;
    }

    @GetMapping
    public List<BienResponse> lister(Authentication auth) {
        return bienService.lister(uid(auth));
    }

    @GetMapping("/{id}")
    public BienResponse detail(Authentication auth, @PathVariable UUID id) {
        return bienService.detail(uid(auth), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BienResponse creer(Authentication auth, @Valid @RequestBody BienRequest request) {
        return bienService.creer(uid(auth), request);
    }

    @PutMapping("/{id}")
    public BienResponse modifier(Authentication auth, @PathVariable UUID id, @Valid @RequestBody BienRequest request) {
        return bienService.modifier(uid(auth), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(Authentication auth, @PathVariable UUID id) {
        bienService.supprimer(uid(auth), id);
    }

    @PostMapping("/{id}/unites")
    @ResponseStatus(HttpStatus.CREATED)
    public UniteResponse ajouterUnite(Authentication auth, @PathVariable UUID id, @Valid @RequestBody UniteRequest request) {
        return bienService.ajouterUnite(uid(auth), id, request);
    }

    @PutMapping("/{id}/unites/{uniteId}")
    public UniteResponse modifierUnite(
            Authentication auth,
            @PathVariable UUID id,
            @PathVariable UUID uniteId,
            @Valid @RequestBody UniteRequest request) {
        return bienService.modifierUnite(uid(auth), id, uniteId, request);
    }

    @PutMapping("/{id}/unites/{uniteId}/periodicite")
    public UniteResponse periodicite(
            Authentication auth,
            @PathVariable UUID id,
            @PathVariable UUID uniteId,
            @Valid @RequestBody PeriodiciteRequest request) {
        return bienService.periodicite(uid(auth), id, uniteId, request);
    }

    @PutMapping("/{id}/unites/{uniteId}/publication")
    public UniteResponse publication(
            Authentication auth,
            @PathVariable UUID id,
            @PathVariable UUID uniteId,
            @RequestBody PublicationRequest request) {
        return bienService.publier(uid(auth), id, uniteId, request.publie());
    }

    private static UUID uid(Authentication auth) {
        return UUID.fromString(auth.getName());
    }
}
