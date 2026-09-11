package com.location.reservations.controller;

import com.location.reservations.dto.ReservationRequest;
import com.location.reservations.dto.ReservationResponse;
import com.location.reservations.dto.StatutRequest;
import com.location.reservations.service.ReservationService;
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
@RequestMapping("/api/v1")
public class ReservationController {
    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @PostMapping("/public/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse creer(@Valid @RequestBody ReservationRequest request, Authentication auth) {
        UUID userId = auth != null && auth.isAuthenticated() && auth.getName() != null && !"anonymousUser".equals(auth.getName())
                ? UUID.fromString(auth.getName())
                : null;
        try {
            if (userId == null && auth != null && auth.getName() != null && !"anonymousUser".equals(auth.getName())) {
                userId = UUID.fromString(auth.getName());
            }
        } catch (IllegalArgumentException ignored) {
            userId = null;
        }
        return service.creer(request, userId);
    }

    @GetMapping("/reservations")
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public List<ReservationResponse> lister(Authentication auth) {
        return service.listerProprio(UUID.fromString(auth.getName()));
    }

    @PostMapping("/reservations/{id}/decision")
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public ReservationResponse decider(
            Authentication auth, @PathVariable UUID id, @Valid @RequestBody StatutRequest request) {
        return service.decider(UUID.fromString(auth.getName()), id, request.statut());
    }
}
