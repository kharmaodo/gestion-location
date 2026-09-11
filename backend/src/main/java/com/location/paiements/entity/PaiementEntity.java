package com.location.paiements.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "paiements", name = "paiement")
public class PaiementEntity {
    @Id private UUID id;
    @Column(name = "echeance_id", nullable = false) private UUID echeanceId;
    @Column(nullable = false) private BigDecimal montant;
    @Column(nullable = false) private String mode = "ESPECES";
    private String reference;
    @Column(name = "recu_numero", nullable = false) private String recuNumero;
    @Column(name = "paye_le", nullable = false) private Instant payeLe = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEcheanceId() { return echeanceId; }
    public void setEcheanceId(UUID echeanceId) { this.echeanceId = echeanceId; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getRecuNumero() { return recuNumero; }
    public void setRecuNumero(String recuNumero) { this.recuNumero = recuNumero; }
    public Instant getPayeLe() { return payeLe; }
}
