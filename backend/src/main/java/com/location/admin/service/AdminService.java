package com.location.admin.service;

import com.location.admin.dto.AdminUserResponse;
import com.location.identites.entity.UtilisateurEntity;
import com.location.identites.entity.UtilisateurRoleEntity;
import com.location.identites.repository.UtilisateurRepository;
import com.location.identites.repository.UtilisateurRoleRepository;
import com.location.shared.exception.ApiException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {
    private static final Set<String> ROLES = Set.of("ADMIN", "PROPRIETAIRE", "LOCATAIRE", "GESTIONNAIRE");
    private static final Set<String> STATUTS = Set.of("ACTIF", "SUSPENDU");
    private final UtilisateurRepository utilisateurs;
    private final UtilisateurRoleRepository roles;
    private final String bootstrapToken;

    public AdminService(
            UtilisateurRepository utilisateurs,
            UtilisateurRoleRepository roles,
            @Value("${app.admin.bootstrap-token:dev-admin-bootstrap}") String bootstrapToken) {
        this.utilisateurs = utilisateurs;
        this.roles = roles;
        this.bootstrapToken = bootstrapToken;
    }

    @Transactional
    public AdminUserResponse bootstrap(UUID userId, String token) {
        if (!bootstrapToken.equals(token)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "token bootstrap invalide");
        }
        if (roles.existsByRole("ADMIN")) {
            throw new ApiException(HttpStatus.CONFLICT, "un administrateur existe deja");
        }
        grant(userId, "ADMIN");
        return toDto(owned(userId));
    }

    @Transactional(readOnly = true)
    public List<AdminUserResponse> lister() {
        return utilisateurs.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public AdminUserResponse changerStatut(UUID cibleId, String statut) {
        String s = statut.toUpperCase();
        if (!STATUTS.contains(s)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "statut invalide");
        }
        UtilisateurEntity u = owned(cibleId);
        u.setStatut(s);
        utilisateurs.save(u);
        return toDto(u);
    }

    @Transactional
    public AdminUserResponse changerRole(UUID cibleId, String role, String action) {
        String r = role.toUpperCase();
        if (!ROLES.contains(r)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "role invalide");
        }
        if ("GRANT".equalsIgnoreCase(action)) {
            grant(cibleId, r);
        } else if ("REVOKE".equalsIgnoreCase(action)) {
            roles.findByUtilisateurIdAndRole(cibleId, r).ifPresent(roles::delete);
        } else {
            throw new ApiException(HttpStatus.BAD_REQUEST, "action invalide");
        }
        return toDto(owned(cibleId));
    }

    private void grant(UUID userId, String role) {
        if (roles.findByUtilisateurIdAndRole(userId, role).isPresent()) {
            return;
        }
        UtilisateurRoleEntity e = new UtilisateurRoleEntity();
        e.setId(UUID.randomUUID());
        e.setUtilisateurId(userId);
        e.setRole(role);
        roles.save(e);
    }

    private UtilisateurEntity owned(UUID id) {
        return utilisateurs.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
    }

    private AdminUserResponse toDto(UtilisateurEntity u) {
        List<String> rs = roles.findByUtilisateurId(u.getId()).stream().map(UtilisateurRoleEntity::getRole).toList();
        return new AdminUserResponse(u.getId(), u.getEmail(), u.getTelephone(), u.getPrenom(), u.getNom(), u.getStatut(), rs);
    }
}
