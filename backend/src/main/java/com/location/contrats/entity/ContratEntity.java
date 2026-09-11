package com.location.contrats.entity;

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
@Table(schema = "contrats", name = "contrat")
public class ContratEntity {
    @Id private UUID id;
    @Column(name = "proprietaire_id", nullable = false) private UUID proprietaireId;
    @Column(name = "unite_id", nullable = false) private UUID uniteId;
    @Column(name = "dossier_id") private UUID dossierId;
    @Column(name = "reservation_id") private UUID reservationId;
    @Column(name = "date_debut", nullable = false) private LocalDate dateDebut;
    @Column(name = "date_fin") private LocalDate dateFin;
    @Column(nullable = false) private BigDecimal loyer;
    @Column(nullable = false) private String devise = "XOF";
    @Column(nullable = false) private String periodicite;
    @Column(name = "jour_echeance", columnDefinition = "smallint") private Short jourEcheance;
    private BigDecimal caution;
    @Column(nullable = false) private String statut = "BROUILLON";
    @Column(name = "cree_le", nullable = false) private Instant creeLe = Instant.now();
    @Column(name = "maj_le", nullable = false) private Instant majLe = Instant.now();
    @Version private Long version;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProprietaireId() { return proprietaireId; }
    public void setProprietaireId(UUID proprietaireId) { this.proprietaireId = proprietaireId; }
    public UUID getUniteId() { return uniteId; }
    public void setUniteId(UUID uniteId) { this.uniteId = uniteId; }
    public UUID getDossierId() { return dossierId; }
    public void setDossierId(UUID dossierId) { this.dossierId = dossierId; }
    public UUID getReservationId() { return reservationId; }
    public void setReservationId(UUID reservationId) { this.reservationId = reservationId; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public BigDecimal getLoyer() { return loyer; }
    public void setLoyer(BigDecimal loyer) { this.loyer = loyer; }
    public String getDevise() { return devise; }
    public String getPeriodicite() { return periodicite; }
    public void setPeriodicite(String periodicite) { this.periodicite = periodicite; }
    public Integer getJourEcheance() { return jourEcheance == null ? null : jourEcheance.intValue(); }
    public void setJourEcheance(Integer jourEcheance) {
        this.jourEcheance = jourEcheance == null ? null : jourEcheance.shortValue();
    }
    public BigDecimal getCaution() { return caution; }
    public void setCaution(BigDecimal caution) { this.caution = caution; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public Instant getCreeLe() { return creeLe; }
    public void setMajLe(Instant majLe) { this.majLe = majLe; }
}
