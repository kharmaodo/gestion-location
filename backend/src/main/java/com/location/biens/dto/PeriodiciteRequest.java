package com.location.biens.dto;

import jakarta.validation.constraints.NotBlank;

public record PeriodiciteRequest(@NotBlank String periodicite, Integer jourEcheance) {}
