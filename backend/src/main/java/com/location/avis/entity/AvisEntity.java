package com.location.avis.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "avis", name = "avis")
public class AvisEntity {
    @Id private UUID id;
    @Column(name = "auteur_id", nullable = false) private UUID auteurId;
    @Column(name = "cible_unite_id") private UUID cibleUniteId;
    @Column(name = "cible_utilisateur_id") private UUID cibleUtilisateurId;
    @Column(nullable = false, columnDefinition = "smallint") private Short note;
    private String commentaire;
    @Column(name = "cree_le", nullable = false) private Instant creeLe = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAuteurId() { return auteurId; }
    public void setAuteurId(UUID auteurId) { this.auteurId = auteurId; }
    public UUID getCibleUniteId() { return cibleUniteId; }
    public void setCibleUniteId(UUID cibleUniteId) { this.cibleUniteId = cibleUniteId; }
    public UUID getCibleUtilisateurId() { return cibleUtilisateurId; }
    public void setCibleUtilisateurId(UUID cibleUtilisateurId) { this.cibleUtilisateurId = cibleUtilisateurId; }
    public int getNote() { return note == null ? 0 : note.intValue(); }
    public void setNote(int note) { this.note = (short) note; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    public Instant getCreeLe() { return creeLe; }
}
