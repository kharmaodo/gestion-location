package com.location.paiements.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(schema = "paiements", name = "echeance")
public class EcheanceEntity {
    @Id private UUID id;
    @Column(name = "contrat_id", nullable = false) private UUID contratId;
    @Column(name = "proprietaire_id", nullable = false) private UUID proprietaireId;
    @Column(name = "periode_debut", nullable = false) private LocalDate periodeDebut;
    @Column(name = "periode_fin", nullable = false) private LocalDate periodeFin;
    @Column(nullable = false) private BigDecimal montant;
    @Column(nullable = false) private String devise = "XOF";
    @Column(nullable = false) private String statut = "A_PAYER";
    @Column(name = "cree_le", nullable = false) private Instant creeLe = Instant.now();
    @Column(name = "maj_le", nullable = false) private Instant majLe = Instant.now();
    @Version private Long version;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getContratId() { return contratId; }
    public void setContratId(UUID contratId) { this.contratId = contratId; }
    public UUID getProprietaireId() { return proprietaireId; }
    public void setProprietaireId(UUID proprietaireId) { this.proprietaireId = proprietaireId; }
    public LocalDate getPeriodeDebut() { return periodeDebut; }
    public void setPeriodeDebut(LocalDate periodeDebut) { this.periodeDebut = periodeDebut; }
    public LocalDate getPeriodeFin() { return periodeFin; }
    public void setPeriodeFin(LocalDate periodeFin) { this.periodeFin = periodeFin; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public String getDevise() { return devise; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public Instant getCreeLe() { return creeLe; }
    public void setMajLe(Instant majLe) { this.majLe = majLe; }
}
