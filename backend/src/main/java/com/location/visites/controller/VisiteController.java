package com.location.visites.controller;

import com.location.visites.dto.VisiteResponse;
import com.location.visites.service.VisiteService;
import java.util.List;
import java.util.Map;
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
@RequestMapping("/api/v1/visites")
@PreAuthorize("hasRole('PROPRIETAIRE')")
public class VisiteController {
    private final VisiteService service;

    public VisiteController(VisiteService service) {
        this.service = service;
    }

    @GetMapping
    public List<VisiteResponse> lister(Authentication auth) {
        return service.lister(UUID.fromString(auth.getName()));
    }

    @PostMapping("/{id}/statut")
    public VisiteResponse statut(
            Authentication auth, @PathVariable UUID id, @RequestBody Map<String, String> body) {
        return service.changerStatut(UUID.fromString(auth.getName()), id, body.getOrDefault("statut", ""));
    }
}
