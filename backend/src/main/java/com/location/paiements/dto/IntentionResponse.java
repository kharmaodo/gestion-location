package com.location.paiements.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record IntentionResponse(UUID id, UUID echeanceId, String fournisseur, BigDecimal montant, String statut, String checkoutUrl) {}
