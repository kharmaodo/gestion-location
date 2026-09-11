package com.location.identites.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "identites", name = "reset_password")
public class ResetPasswordEntity {
    @Id private UUID id;
    @Column(name = "utilisateur_id", nullable = false) private UUID utilisateurId;
    @Column(name = "token_hash", nullable = false, unique = true) private String tokenHash;
    @Column(name = "expire_le", nullable = false) private Instant expireLe;
    @Column(nullable = false) private boolean utilise;
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(UUID utilisateurId) { this.utilisateurId = utilisateurId; }
    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }
    public Instant getExpireLe() { return expireLe; }
    public void setExpireLe(Instant expireLe) { this.expireLe = expireLe; }
    public boolean isUtilise() { return utilise; }
    public void setUtilise(boolean utilise) { this.utilise = utilise; }
}
