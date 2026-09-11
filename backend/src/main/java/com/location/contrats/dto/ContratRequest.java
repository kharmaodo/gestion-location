package com.location.contrats.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ContratRequest(
        @NotNull UUID uniteId,
        UUID dossierId,
        UUID reservationId,
        @NotNull LocalDate dateDebut,
        LocalDate dateFin,
        BigDecimal loyer,
        String periodicite,
        Integer jourEcheance,
        BigDecimal caution) {}
