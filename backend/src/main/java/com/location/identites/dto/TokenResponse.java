package com.location.identites.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        Instant expiresAt,
        UUID userId,
        List<String> roles,
        boolean requiresTwoFactor,
        String pendingToken) {

    public static TokenResponse tokens(String access, String refresh, Instant exp, UUID userId, List<String> roles) {
        return new TokenResponse(access, refresh, exp, userId, roles, false, null);
    }

    public static TokenResponse pending2fa(String pendingToken) {
        return new TokenResponse(null, null, null, null, List.of(), true, pendingToken);
    }
}
