package com.location.contrats.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record RestitutionCautionResponse(
        UUID contratId,
        BigDecimal cautionInitiale,
        BigDecimal coutReparations,
        BigDecimal montantRetenu,
        BigDecimal montantRestitue,
        boolean edlSortieValide,
        String devise) {}
