package com.location.reservations.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(schema = "reservations", name = "reservation")
public class ReservationEntity {
    @Id private UUID id;
    @Column(name = "unite_id", nullable = false) private UUID uniteId;
    @Column(name = "proprietaire_id", nullable = false) private UUID proprietaireId;
    @Column(name = "locataire_user_id") private UUID locataireUserId;
    @Column(nullable = false) private String nom;
    private String prenom;
    private String telephone;
    private String email;
    @Column(name = "date_debut", nullable = false) private LocalDate dateDebut;
    @Column(name = "date_fin", nullable = false) private LocalDate dateFin;
    private String message;
    @Column(nullable = false) private String statut = "EN_ATTENTE";
    @Column(name = "cree_le", nullable = false) private Instant creeLe = Instant.now();
    @Column(name = "maj_le", nullable = false) private Instant majLe = Instant.now();
    @Version private Long version;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUniteId() { return uniteId; }
    public void setUniteId(UUID uniteId) { this.uniteId = uniteId; }
    public UUID getProprietaireId() { return proprietaireId; }
    public void setProprietaireId(UUID proprietaireId) { this.proprietaireId = proprietaireId; }
    public UUID getLocataireUserId() { return locataireUserId; }
    public void setLocataireUserId(UUID locataireUserId) { this.locataireUserId = locataireUserId; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public Instant getCreeLe() { return creeLe; }
    public void setMajLe(Instant majLe) { this.majLe = majLe; }
}
