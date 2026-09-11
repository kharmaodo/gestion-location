package com.location.contrats.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record EtatLieuxResponse(
        UUID id, UUID contratId, String type, String observations, BigDecimal coutReparations, String statut, Instant creeLe) {}
