package com.location.visites.dto;

import java.time.Instant;
import java.util.UUID;

public record VisiteResponse(
        UUID id, UUID uniteId, String nom, String telephone, String email, Instant creneau, String statut) {}
