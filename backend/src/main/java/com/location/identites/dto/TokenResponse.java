package com.location.identites.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        Instant expiresAt,
        UUID userId,
        List<String> roles) {}
