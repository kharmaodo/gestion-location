package com.location.avis.dto;

import java.time.Instant;
import java.util.UUID;

public record AvisResponse(UUID id, UUID auteurId, UUID cibleUniteId, int note, String commentaire, Instant creeLe) {}
