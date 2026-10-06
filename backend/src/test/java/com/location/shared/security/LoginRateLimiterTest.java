package com.location.shared.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LoginRateLimiterTest {
    @Test
    void bloqueApresLePlafond() {
        LoginRateLimiter limiter = new LoginRateLimiter(2);
        assertTrue(limiter.allow("127.0.0.1"));
        assertTrue(limiter.allow("127.0.0.1"));
        assertFalse(limiter.allow("127.0.0.1"));
        assertTrue(limiter.allow("10.0.0.2"));
    }
}
