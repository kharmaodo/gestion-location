package com.location.contrats.dto;

import java.math.BigDecimal;

public record CautionSimulationResponse(
        BigDecimal loyer,
        String periodicite,
        BigDecimal equivalentMensuel,
        int moisDemandes,
        int moisRetenus,
        BigDecimal plafond,
        BigDecimal cautionCalculee,
        String devise,
        String regle) {}
