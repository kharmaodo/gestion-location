package com.location.messagerie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "messagerie", name = "message")
public class MessageEntity {
    @Id private UUID id;
    @Column(name = "conversation_id", nullable = false) private UUID conversationId;
    @Column(name = "auteur_id", nullable = false) private UUID auteurId;
    @Column(nullable = false) private String corps;
    @Column(name = "cree_le", nullable = false) private Instant creeLe = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getConversationId() { return conversationId; }
    public void setConversationId(UUID conversationId) { this.conversationId = conversationId; }
    public UUID getAuteurId() { return auteurId; }
    public void setAuteurId(UUID auteurId) { this.auteurId = auteurId; }
    public String getCorps() { return corps; }
    public void setCorps(String corps) { this.corps = corps; }
    public Instant getCreeLe() { return creeLe; }
}
