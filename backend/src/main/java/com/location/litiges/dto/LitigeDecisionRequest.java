package com.location.litiges.dto;

import jakarta.validation.constraints.NotBlank;

public record LitigeDecisionRequest(@NotBlank String statut, String decision) {}
