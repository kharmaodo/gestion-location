package com.location.litiges.controller;

import com.location.litiges.dto.LitigeDecisionRequest;
import com.location.litiges.dto.LitigeRequest;
import com.location.litiges.dto.LitigeResponse;
import com.location.litiges.service.LitigeService;
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
@RequestMapping("/api/v1/litiges")
@PreAuthorize("hasRole('PROPRIETAIRE')")
public class LitigeController {
    private final LitigeService service;

    public LitigeController(LitigeService service) {
        this.service = service;
    }

    @GetMapping
    public List<LitigeResponse> lister(Authentication auth) {
        return service.lister(UUID.fromString(auth.getName()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LitigeResponse ouvrir(Authentication auth, @Valid @RequestBody LitigeRequest request) {
        return service.ouvrir(UUID.fromString(auth.getName()), request);
    }

    @PostMapping("/{id}/decision")
    public LitigeResponse decider(
            Authentication auth, @PathVariable UUID id, @Valid @RequestBody LitigeDecisionRequest request) {
        return service.decider(UUID.fromString(auth.getName()), id, request);
    }
}
