package com.location.avis.controller;

import com.location.avis.dto.AvisRequest;
import com.location.avis.dto.AvisResponse;
import com.location.avis.service.AvisService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AvisController {
    private final AvisService service;

    public AvisController(AvisService service) {
        this.service = service;
    }

    @GetMapping("/public/annonces/{uniteId}/avis")
    public List<AvisResponse> publics(@PathVariable UUID uniteId) {
        return service.listerUnite(uniteId);
    }

    @PostMapping("/avis")
    @ResponseStatus(HttpStatus.CREATED)
    public AvisResponse publier(Authentication auth, @Valid @RequestBody AvisRequest request) {
        return service.publier(UUID.fromString(auth.getName()), request);
    }
}
