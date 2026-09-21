package com.location.contrats.controller;

import com.location.contrats.dto.ContratResponse;
import com.location.contrats.service.ContratService;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MesContratsController {
    private final ContratService service;

    public MesContratsController(ContratService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/mes-contrats")
    @PreAuthorize("hasAnyRole('PROPRIETAIRE','LOCATAIRE')")
    public List<ContratResponse> mes(Authentication auth) {
        return service.listerPourUtilisateur(UUID.fromString(auth.getName()), auth.getAuthorities().toString());
    }
}
