package com.location.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record StatutRequest(@NotBlank String statut) {}
