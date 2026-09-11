package com.location.identites.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "identites", name = "consentement")
public class ConsentementEntity {
    @Id private UUID id;
    @Column(name = "utilisateur_id", nullable = false) private UUID utilisateurId;
    @Column(nullable = false) private String type;
    @Column(nullable = false) private String source;
    @Column(nullable = false) private boolean accepte;
    @Column(nullable = false) private Instant horodatage = Instant.now();
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public void setUtilisateurId(UUID utilisateurId) { this.utilisateurId = utilisateurId; }
    public void setType(String type) { this.type = type; }
    public void setSource(String source) { this.source = source; }
    public void setAccepte(boolean accepte) { this.accepte = accepte; }
}
