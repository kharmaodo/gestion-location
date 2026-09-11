package com.location.paiements.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record EcheanceResponse(
        UUID id,
        UUID contratId,
        LocalDate periodeDebut,
        LocalDate periodeFin,
        BigDecimal montant,
        String devise,
        String statut,
        List<PaiementResponse> paiements) {}
