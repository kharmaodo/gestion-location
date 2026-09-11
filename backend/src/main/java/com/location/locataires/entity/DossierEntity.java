package com.location.locataires.entity;

import com.location.shared.crypto.EncryptedStringConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "locataires", name = "dossier")
public class DossierEntity {
    @Id private UUID id;
    @Column(name = "proprietaire_id", nullable = false) private UUID proprietaireId;
    @Column(name = "utilisateur_id") private UUID utilisateurId;
    private String prenom;
    @Column(nullable = false) private String nom;
    private String telephone;
    private String email;
    @Column(name = "piece_type") private String pieceType;
    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "piece_numero", columnDefinition = "text")
    private String pieceNumero;
    @Column(name = "kyc_statut", nullable = false) private String kycStatut = "EN_ATTENTE";
    @Column(name = "kyc_commentaire") private String kycCommentaire;
    @Column(name = "cree_le", nullable = false) private Instant creeLe = Instant.now();
    @Column(name = "maj_le", nullable = false) private Instant majLe = Instant.now();
    @Version private Long version;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProprietaireId() { return proprietaireId; }
    public void setProprietaireId(UUID proprietaireId) { this.proprietaireId = proprietaireId; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPieceType() { return pieceType; }
    public void setPieceType(String pieceType) { this.pieceType = pieceType; }
    public String getPieceNumero() { return pieceNumero; }
    public void setPieceNumero(String pieceNumero) { this.pieceNumero = pieceNumero; }
    public String getKycStatut() { return kycStatut; }
    public void setKycStatut(String kycStatut) { this.kycStatut = kycStatut; }
    public String getKycCommentaire() { return kycCommentaire; }
    public void setKycCommentaire(String kycCommentaire) { this.kycCommentaire = kycCommentaire; }
    public void setMajLe(Instant majLe) { this.majLe = majLe; }
}
