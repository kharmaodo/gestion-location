package com.location.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final JwtProperties properties;
    private final SecretKey key;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(padSecret(properties.secret()).getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(UUID userId, String subject, List<String> roles) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(properties.accessMinutes() * 60);
        return Jwts.builder()
                .subject(subject)
                .claim("uid", userId.toString())
                .claim("roles", roles)
                .claim("typ", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    public String createPending2faToken(UUID userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId.toString())
                .claim("uid", userId.toString())
                .claim("typ", "2fa_pending")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(300)))
                .signWith(key)
                .compact();
    }

    public UUID requirePending2fa(String token) {
        Claims claims = parse(token);
        if (!"2fa_pending".equals(claims.get("typ", String.class))) {
            throw new IllegalArgumentException("token 2FA invalide");
        }
        return UUID.fromString(claims.get("uid", String.class));
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public Instant accessExpiry() {
        return Instant.now().plusSeconds(properties.accessMinutes() * 60);
    }

    public Instant refreshExpiry() {
        return Instant.now().plusSeconds(properties.refreshDays() * 86400);
    }

    private static String padSecret(String secret) {
        if (secret.length() >= 32) return secret;
        return secret + "0".repeat(32 - secret.length());
    }
}
