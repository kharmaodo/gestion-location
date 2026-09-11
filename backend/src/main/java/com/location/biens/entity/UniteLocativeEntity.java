package com.location.biens.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "biens", name = "unite_locative")
public class UniteLocativeEntity {
    @Id private UUID id;
    @Column(name = "bien_id", nullable = false) private UUID bienId;
    @Column(nullable = false) private String libelle;
    @Column(nullable = false) private String type;
    @Column(name = "surface_m2") private BigDecimal surfaceM2;
    @Column(nullable = false) private boolean meuble;
    @Column(nullable = false) private BigDecimal loyer;
    @Column(nullable = false) private String devise = "XOF";
    @Column(nullable = false) private String periodicite = "MENSUEL";
    @Column(name = "jour_echeance") private Integer jourEcheance;
    @Column(nullable = false) private String statut = "LIBRE";
    @Column(nullable = false) private boolean publie;
    @Column(name = "cree_le", nullable = false) private Instant creeLe = Instant.now();
    @Column(name = "maj_le", nullable = false) private Instant majLe = Instant.now();
    @Version private Long version;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getBienId() { return bienId; }
    public void setBienId(UUID bienId) { this.bienId = bienId; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public BigDecimal getSurfaceM2() { return surfaceM2; }
    public void setSurfaceM2(BigDecimal surfaceM2) { this.surfaceM2 = surfaceM2; }
    public boolean isMeuble() { return meuble; }
    public void setMeuble(boolean meuble) { this.meuble = meuble; }
    public BigDecimal getLoyer() { return loyer; }
    public void setLoyer(BigDecimal loyer) { this.loyer = loyer; }
    public String getDevise() { return devise; }
    public String getPeriodicite() { return periodicite; }
    public void setPeriodicite(String periodicite) { this.periodicite = periodicite; }
    public Integer getJourEcheance() { return jourEcheance; }
    public void setJourEcheance(Integer jourEcheance) { this.jourEcheance = jourEcheance; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public boolean isPublie() { return publie; }
    public void setPublie(boolean publie) { this.publie = publie; }
    public void setMajLe(Instant majLe) { this.majLe = majLe; }
}
