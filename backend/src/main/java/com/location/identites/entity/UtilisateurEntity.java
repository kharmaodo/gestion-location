package com.location.identites.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "identites", name = "utilisateur")
public class UtilisateurEntity {
    @Id private UUID id;
    @Column(unique = true) private String email;
    @Column(unique = true) private String telephone;
    @Column(name = "mot_de_passe_hash", nullable = false) private String motDePasseHash;
    private String prenom;
    private String nom;
    @Column(nullable = false) private String statut = "ACTIF";
    @Column(name = "email_verifie", nullable = false) private boolean emailVerifie;
    @Column(name = "telephone_verifie", nullable = false) private boolean telephoneVerifie;
    @Column(name = "two_factor_active", nullable = false) private boolean twoFactorActive;
    @Column(name = "two_factor_secret") private String twoFactorSecret;
    @Column(name = "cree_le", nullable = false) private Instant creeLe = Instant.now();
    @Column(name = "maj_le", nullable = false) private Instant majLe = Instant.now();
    @Version private Long version;
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getMotDePasseHash() { return motDePasseHash; }
    public void setMotDePasseHash(String motDePasseHash) { this.motDePasseHash = motDePasseHash; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public boolean isTwoFactorActive() { return twoFactorActive; }
    public void setTwoFactorActive(boolean twoFactorActive) { this.twoFactorActive = twoFactorActive; }
}
