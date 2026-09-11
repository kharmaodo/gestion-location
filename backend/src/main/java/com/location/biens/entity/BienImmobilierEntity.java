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
@Table(schema = "biens", name = "bien_immobilier")
public class BienImmobilierEntity {
    @Id private UUID id;
    @Column(name = "proprietaire_id", nullable = false) private UUID proprietaireId;
    @Column(nullable = false) private String designation;
    @Column(nullable = false) private String type;
    private String adresse;
    private String ville;
    private BigDecimal latitude;
    private BigDecimal longitude;
    @Column(nullable = false) private String statut = "BROUILLON";
    @Column(name = "cree_le", nullable = false) private Instant creeLe = Instant.now();
    @Column(name = "maj_le", nullable = false) private Instant majLe = Instant.now();
    @Version private Long version;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProprietaireId() { return proprietaireId; }
    public void setProprietaireId(UUID proprietaireId) { this.proprietaireId = proprietaireId; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public Instant getMajLe() { return majLe; }
    public void setMajLe(Instant majLe) { this.majLe = majLe; }
}
