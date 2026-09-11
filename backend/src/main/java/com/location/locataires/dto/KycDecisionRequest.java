package com.location.locataires.dto;

import jakarta.validation.constraints.NotBlank;

public record KycDecisionRequest(@NotBlank String statut, String commentaire) {}
