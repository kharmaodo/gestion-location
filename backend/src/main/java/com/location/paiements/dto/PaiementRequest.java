package com.location.paiements.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record PaiementRequest(@NotNull @Positive BigDecimal montant, String mode, String reference) {}
