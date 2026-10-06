package com.location.shared.security;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LoginRateLimiter {
    private final int maxPerMinute;
    private final Map<String, Deque<Long>> hits = new ConcurrentHashMap<>();

    public LoginRateLimiter(@Value("${app.ratelimit.login-per-minute:30}") int maxPerMinute) {
        this.maxPerMinute = maxPerMinute;
    }

    public boolean allow(String key) {
        long now = Instant.now().toEpochMilli();
        Deque<Long> window = hits.computeIfAbsent(key, ignored -> new ArrayDeque<>());
        synchronized (window) {
            while (!window.isEmpty() && now - window.peekFirst() > 60_000) {
                window.removeFirst();
            }
            if (window.size() >= maxPerMinute) {
                return false;
            }
            window.addLast(now);
            return true;
        }
    }
}
