package com.location.identites.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "identites", name = "refresh_token")
public class RefreshTokenEntity {
    @Id private UUID id;
    @Column(name = "utilisateur_id", nullable = false) private UUID utilisateurId;
    @Column(name = "jti_hash", nullable = false, unique = true) private String jtiHash;
    @Column(name = "expire_le", nullable = false) private Instant expireLe;
    @Column(nullable = false) private boolean revoque;
    @Column(name = "cree_le", nullable = false) private Instant creeLe = Instant.now();
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(UUID utilisateurId) { this.utilisateurId = utilisateurId; }
    public String getJtiHash() { return jtiHash; }
    public void setJtiHash(String jtiHash) { this.jtiHash = jtiHash; }
    public Instant getExpireLe() { return expireLe; }
    public void setExpireLe(Instant expireLe) { this.expireLe = expireLe; }
    public boolean isRevoque() { return revoque; }
    public void setRevoque(boolean revoque) { this.revoque = revoque; }
}
