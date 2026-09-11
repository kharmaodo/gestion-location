package com.location.paiements.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record WebhookRequest(@NotNull UUID intentionId, @NotNull String statut) {}
