package com.location.contrats.controller;

import com.location.contrats.dto.ContactReveleResponse;
import com.location.contrats.service.ContactReveleService;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactReveleController {
    private final ContactReveleService service;

    public ContactReveleController(ContactReveleService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/contrats/{id}/contacts")
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public ContactReveleResponse contacts(Authentication auth, @PathVariable UUID id) {
        return service.contacts(UUID.fromString(auth.getName()), id);
    }
}
