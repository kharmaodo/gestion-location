package com.location.biens.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "biens", name = "media")
public class MediaEntity {
    @Id private UUID id;
    @Column(name = "unite_id", nullable = false) private UUID uniteId;
    @Column(nullable = false) private String url;
    @Column(nullable = false) private String type = "PHOTO";
    @Column(nullable = false) private int position;
    @Column(name = "cree_le", nullable = false) private Instant creeLe = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUniteId() { return uniteId; }
    public void setUniteId(UUID uniteId) { this.uniteId = uniteId; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}
