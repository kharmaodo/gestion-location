package com.location.messagerie.dto;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(UUID id, UUID auteurId, String corps, Instant creeLe) {}
