package com.location.paiements.dto;

import jakarta.validation.constraints.NotBlank;

public record IntentionRequest(@NotBlank String fournisseur) {}
