package com.location.avis.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AvisRequest(@NotNull UUID cibleUniteId, @Min(1) @Max(5) int note, String commentaire) {}
