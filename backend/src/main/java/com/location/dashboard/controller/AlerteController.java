package com.location.dashboard.controller;

import com.location.dashboard.dto.AlerteResponse;
import com.location.dashboard.service.AlerteService;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AlerteController {
    private final AlerteService service;

    public AlerteController(AlerteService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/dashboard/alertes")
    @PreAuthorize("hasAnyRole('PROPRIETAIRE','ADMIN')")
    public List<AlerteResponse> lister(Authentication auth) {
        return service.lister(UUID.fromString(auth.getName()));
    }
}
