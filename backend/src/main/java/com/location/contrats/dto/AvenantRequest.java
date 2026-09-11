package com.location.contrats.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AvenantRequest(
        @NotBlank String motif,
        String periodicite,
        BigDecimal loyer,
        @NotNull LocalDate dateEffet) {}
