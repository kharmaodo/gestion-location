package com.location.litiges.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "litiges", name = "litige")
public class LitigeEntity {
    @Id private UUID id;
    @Column(name = "contrat_id", nullable = false) private UUID contratId;
    @Column(name = "proprietaire_id", nullable = false) private UUID proprietaireId;
    @Column(name = "auteur_id", nullable = false) private UUID auteurId;
    @Column(nullable = false) private String motif;
    @Column(nullable = false) private String description;
    @Column(nullable = false) private String statut = "OUVERT";
    private String decision;
    @Column(name = "cree_le", nullable = false) private Instant creeLe = Instant.now();
    @Column(name = "maj_le", nullable = false) private Instant majLe = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getContratId() { return contratId; }
    public void setContratId(UUID contratId) { this.contratId = contratId; }
    public UUID getProprietaireId() { return proprietaireId; }
    public void setProprietaireId(UUID proprietaireId) { this.proprietaireId = proprietaireId; }
    public UUID getAuteurId() { return auteurId; }
    public void setAuteurId(UUID auteurId) { this.auteurId = auteurId; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public Instant getCreeLe() { return creeLe; }
    public void setMajLe(Instant majLe) { this.majLe = majLe; }
}
