package com.location.litiges.dto;

import java.time.Instant;
import java.util.UUID;

public record LitigeResponse(
        UUID id, UUID contratId, UUID auteurId, String motif, String description, String statut, String decision, Instant creeLe) {}
