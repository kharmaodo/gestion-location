package com.location.notifications.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(UUID id, String type, String message, boolean lu, Instant creeLe) {}
