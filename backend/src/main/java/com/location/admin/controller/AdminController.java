package com.location.admin.controller;

import com.location.admin.dto.AdminUserResponse;
import com.location.admin.dto.BootstrapRequest;
import com.location.admin.dto.RoleRequest;
import com.location.admin.dto.StatutRequest;
import com.location.admin.service.AdminService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }

    @PostMapping("/bootstrap")
    public AdminUserResponse bootstrap(Authentication auth, @Valid @RequestBody BootstrapRequest request) {
        return service.bootstrap(UUID.fromString(auth.getName()), request.token());
    }

    @GetMapping("/utilisateurs")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminUserResponse> lister() {
        return service.lister();
    }

    @PostMapping("/utilisateurs/{id}/statut")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminUserResponse statut(@PathVariable UUID id, @Valid @RequestBody StatutRequest request) {
        return service.changerStatut(id, request.statut());
    }

    @PostMapping("/utilisateurs/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminUserResponse roles(@PathVariable UUID id, @Valid @RequestBody RoleRequest request) {
        return service.changerRole(id, request.role(), request.action());
    }
}
