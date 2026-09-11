package com.location.contrats.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record EtatLieuxRequest(
        @NotNull UUID contratId, @NotBlank String type, String observations, BigDecimal coutReparations) {}
