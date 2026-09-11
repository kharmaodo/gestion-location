package com.location.messagerie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "messagerie", name = "conversation")
public class ConversationEntity {
    @Id private UUID id;
    @Column(name = "participant_a", nullable = false) private UUID participantA;
    @Column(name = "participant_b", nullable = false) private UUID participantB;
    @Column(name = "unite_id") private UUID uniteId;
    @Column(name = "cree_le", nullable = false) private Instant creeLe = Instant.now();
    @Column(name = "maj_le", nullable = false) private Instant majLe = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getParticipantA() { return participantA; }
    public void setParticipantA(UUID participantA) { this.participantA = participantA; }
    public UUID getParticipantB() { return participantB; }
    public void setParticipantB(UUID participantB) { this.participantB = participantB; }
    public UUID getUniteId() { return uniteId; }
    public void setUniteId(UUID uniteId) { this.uniteId = uniteId; }
    public Instant getMajLe() { return majLe; }
    public void setMajLe(Instant majLe) { this.majLe = majLe; }
}
