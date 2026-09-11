package com.location.contrats.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AvenantResponse(
        UUID id, String motif, String periodicite, BigDecimal loyer, LocalDate dateEffet, Instant creeLe) {}
