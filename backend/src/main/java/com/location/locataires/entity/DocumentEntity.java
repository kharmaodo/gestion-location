package com.location.locataires.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "locataires", name = "document")
public class DocumentEntity {
    @Id private UUID id;
    @Column(name = "dossier_id", nullable = false) private UUID dossierId;
    @Column(nullable = false) private String type;
    @Column(name = "nom_fichier", nullable = false) private String nomFichier;
    @Column(nullable = false) private String chemin;
    private String mime;
    @Column(name = "kyc_statut", nullable = false) private String kycStatut = "EN_ATTENTE";
    @Column(name = "cree_le", nullable = false) private Instant creeLe = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getDossierId() { return dossierId; }
    public void setDossierId(UUID dossierId) { this.dossierId = dossierId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getNomFichier() { return nomFichier; }
    public void setNomFichier(String nomFichier) { this.nomFichier = nomFichier; }
    public String getChemin() { return chemin; }
    public void setChemin(String chemin) { this.chemin = chemin; }
    public String getMime() { return mime; }
    public void setMime(String mime) { this.mime = mime; }
    public String getKycStatut() { return kycStatut; }
    public void setKycStatut(String kycStatut) { this.kycStatut = kycStatut; }
    public Instant getCreeLe() { return creeLe; }
}
