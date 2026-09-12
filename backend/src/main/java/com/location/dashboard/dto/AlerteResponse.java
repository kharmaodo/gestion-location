package com.location.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AlerteResponse(
        String type,
        String niveau,
        UUID echeanceId,
        UUID contratId,
        LocalDate periodeFin,
        String statutEcheance,
        BigDecimal montant,
        String message) {}
