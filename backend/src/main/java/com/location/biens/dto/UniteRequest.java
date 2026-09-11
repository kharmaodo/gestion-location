package com.location.biens.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record UniteRequest(
        @NotBlank String libelle,
        @NotBlank String type,
        BigDecimal surfaceM2,
        boolean meuble,
        @NotNull @Positive BigDecimal loyer,
        String periodicite,
        Integer jourEcheance) {}
