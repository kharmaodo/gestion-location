package com.location.visites.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "visites", name = "visite")
public class VisiteEntity {
    @Id private UUID id;
    @Column(name = "unite_id", nullable = false) private UUID uniteId;
    @Column(name = "proprietaire_id", nullable = false) private UUID proprietaireId;
    @Column(nullable = false) private String nom;
    private String telephone;
    private String email;
    @Column(nullable = false) private Instant creneau;
    @Column(nullable = false) private String statut = "DEMANDEE";
    @Column(name = "cree_le", nullable = false) private Instant creeLe = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUniteId() { return uniteId; }
    public void setUniteId(UUID uniteId) { this.uniteId = uniteId; }
    public UUID getProprietaireId() { return proprietaireId; }
    public void setProprietaireId(UUID proprietaireId) { this.proprietaireId = proprietaireId; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Instant getCreneau() { return creneau; }
    public void setCreneau(Instant creneau) { this.creneau = creneau; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public Instant getCreeLe() { return creeLe; }
}
