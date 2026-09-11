package com.location.paiements.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaiementResponse(
        UUID id, BigDecimal montant, String mode, String reference, String recuNumero, Instant payeLe) {}
