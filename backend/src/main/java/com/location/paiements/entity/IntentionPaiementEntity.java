package com.location.paiements.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "paiements", name = "intention")
public class IntentionPaiementEntity {
    @Id private UUID id;
    @Column(name = "echeance_id", nullable = false) private UUID echeanceId;
    @Column(name = "proprietaire_id", nullable = false) private UUID proprietaireId;
    @Column(nullable = false) private String fournisseur;
    @Column(nullable = false) private BigDecimal montant;
    @Column(nullable = false) private String statut = "EN_ATTENTE";
    @Column(name = "reference_externe") private String referenceExterne;
    @Column(name = "cree_le", nullable = false) private Instant creeLe = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEcheanceId() { return echeanceId; }
    public void setEcheanceId(UUID echeanceId) { this.echeanceId = echeanceId; }
    public UUID getProprietaireId() { return proprietaireId; }
    public void setProprietaireId(UUID proprietaireId) { this.proprietaireId = proprietaireId; }
    public String getFournisseur() { return fournisseur; }
    public void setFournisseur(String fournisseur) { this.fournisseur = fournisseur; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getReferenceExterne() { return referenceExterne; }
    public void setReferenceExterne(String referenceExterne) { this.referenceExterne = referenceExterne; }
}
