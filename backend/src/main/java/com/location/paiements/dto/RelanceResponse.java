package com.location.paiements.dto;

import java.time.Instant;
import java.util.UUID;

public record RelanceResponse(UUID id, UUID echeanceId, String canal, String message, Instant envoyeeLe) {}
