package com.location.contrats.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "contrats", name = "signature")
public class SignatureEntity {
    @Id private UUID id;
    @Column(name = "contrat_id", nullable = false) private UUID contratId;
    @Column(name = "proprietaire_id", nullable = false) private UUID proprietaireId;
    @Column(name = "role_signataire", nullable = false) private String roleSignataire;
    @Column(name = "nom_signataire", nullable = false) private String nomSignataire;
    @Column(name = "token_hash", nullable = false) private String tokenHash;
    @Column(name = "signe_le") private Instant signeLe;
    @Column(nullable = false) private String statut = "EN_ATTENTE";
    @Column(name = "cree_le", nullable = false) private Instant creeLe = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getContratId() { return contratId; }
    public void setContratId(UUID contratId) { this.contratId = contratId; }
    public UUID getProprietaireId() { return proprietaireId; }
    public void setProprietaireId(UUID proprietaireId) { this.proprietaireId = proprietaireId; }
    public String getRoleSignataire() { return roleSignataire; }
    public void setRoleSignataire(String roleSignataire) { this.roleSignataire = roleSignataire; }
    public String getNomSignataire() { return nomSignataire; }
    public void setNomSignataire(String nomSignataire) { this.nomSignataire = nomSignataire; }
    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }
    public Instant getSigneLe() { return signeLe; }
    public void setSigneLe(Instant signeLe) { this.signeLe = signeLe; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
