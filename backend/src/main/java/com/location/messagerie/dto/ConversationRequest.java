package com.location.messagerie.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ConversationRequest(@NotNull UUID destinataireId, UUID uniteId) {}
