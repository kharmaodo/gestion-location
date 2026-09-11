package com.location.contrats.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "contrats", name = "etat_lieux")
public class EtatLieuxEntity {
    @Id private UUID id;
    @Column(name = "contrat_id", nullable = false) private UUID contratId;
    @Column(name = "proprietaire_id", nullable = false) private UUID proprietaireId;
    @Column(nullable = false) private String type;
    private String observations;
    @Column(name = "cout_reparations", nullable = false) private BigDecimal coutReparations = BigDecimal.ZERO;
    @Column(nullable = false) private String statut = "BROUILLON";
    @Column(name = "cree_le", nullable = false) private Instant creeLe = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getContratId() { return contratId; }
    public void setContratId(UUID contratId) { this.contratId = contratId; }
    public UUID getProprietaireId() { return proprietaireId; }
    public void setProprietaireId(UUID proprietaireId) { this.proprietaireId = proprietaireId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    public BigDecimal getCoutReparations() { return coutReparations; }
    public void setCoutReparations(BigDecimal coutReparations) { this.coutReparations = coutReparations; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public Instant getCreeLe() { return creeLe; }
}
