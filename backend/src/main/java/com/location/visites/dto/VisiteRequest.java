package com.location.visites.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record VisiteRequest(
        @NotNull UUID uniteId,
        @NotBlank String nom,
        String telephone,
        String email,
        @NotNull Instant creneau) {}
