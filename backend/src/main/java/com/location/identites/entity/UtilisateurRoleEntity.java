package com.location.identites.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(schema = "identites", name = "utilisateur_role")
public class UtilisateurRoleEntity {
    @Id private UUID id;
    @Column(name = "utilisateur_id", nullable = false) private UUID utilisateurId;
    @Column(nullable = false) private String role;
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(UUID utilisateurId) { this.utilisateurId = utilisateurId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
