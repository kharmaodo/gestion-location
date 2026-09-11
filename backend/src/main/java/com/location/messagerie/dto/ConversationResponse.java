package com.location.messagerie.dto;

import java.util.List;
import java.util.UUID;

public record ConversationResponse(
        UUID id, UUID participantA, UUID participantB, UUID uniteId, List<MessageResponse> messages) {}
