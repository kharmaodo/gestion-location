package com.location.biens.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record BienRequest(
        @NotBlank String designation,
        @NotBlank String type,
        String adresse,
        String ville,
        BigDecimal latitude,
        BigDecimal longitude) {}
