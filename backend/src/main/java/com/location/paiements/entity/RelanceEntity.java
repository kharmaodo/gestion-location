package com.location.paiements.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "paiements", name = "relance")
public class RelanceEntity {
    @Id private UUID id;
    @Column(name = "echeance_id", nullable = false) private UUID echeanceId;
    @Column(nullable = false) private String canal = "EMAIL";
    @Column(nullable = false) private String message;
    @Column(name = "envoyee_le", nullable = false) private Instant envoyeeLe = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEcheanceId() { return echeanceId; }
    public void setEcheanceId(UUID echeanceId) { this.echeanceId = echeanceId; }
    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Instant getEnvoyeeLe() { return envoyeeLe; }
}
