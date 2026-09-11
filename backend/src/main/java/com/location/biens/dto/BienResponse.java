package com.location.biens.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record BienResponse(
        UUID id,
        String designation,
        String type,
        String adresse,
        String ville,
        BigDecimal latitude,
        BigDecimal longitude,
        String statut,
        long unites,
        long unitesLibres,
        List<UniteResponse> unitesDetail) {}
