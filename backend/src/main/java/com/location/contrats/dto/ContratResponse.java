package com.location.contrats.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ContratResponse(
        UUID id,
        UUID uniteId,
        UUID dossierId,
        UUID reservationId,
        LocalDate dateDebut,
        LocalDate dateFin,
        BigDecimal loyer,
        String devise,
        String periodicite,
        Integer jourEcheance,
        BigDecimal caution,
        String statut,
        List<AvenantResponse> avenants) {}
