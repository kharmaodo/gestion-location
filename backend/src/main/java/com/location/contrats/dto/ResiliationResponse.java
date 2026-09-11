package com.location.contrats.dto;

import java.math.BigDecimal;

public record ResiliationResponse(ContratResponse contrat, BigDecimal penalite, String motifPenalite) {}
