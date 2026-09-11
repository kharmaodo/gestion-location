package com.location.contrats.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(schema = "contrats", name = "avenant")
public class AvenantEntity {
    @Id private UUID id;
    @Column(name = "contrat_id", nullable = false) private UUID contratId;
    @Column(nullable = false) private String motif;
    private String periodicite;
    private BigDecimal loyer;
    @Column(name = "date_effet", nullable = false) private LocalDate dateEffet;
    @Column(name = "cree_le", nullable = false) private Instant creeLe = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getContratId() { return contratId; }
    public void setContratId(UUID contratId) { this.contratId = contratId; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public String getPeriodicite() { return periodicite; }
    public void setPeriodicite(String periodicite) { this.periodicite = periodicite; }
    public BigDecimal getLoyer() { return loyer; }
    public void setLoyer(BigDecimal loyer) { this.loyer = loyer; }
    public LocalDate getDateEffet() { return dateEffet; }
    public void setDateEffet(LocalDate dateEffet) { this.dateEffet = dateEffet; }
    public Instant getCreeLe() { return creeLe; }
}
