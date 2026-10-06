package com.location.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {
    private final LoginRateLimiter limiter;

    public LoginRateLimitFilter(LoginRateLimiter limiter) {
        this.limiter = limiter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if ("POST".equals(request.getMethod()) && request.getRequestURI().endsWith("/auth/login")) {
            String key = request.getRemoteAddr() == null ? "local" : request.getRemoteAddr();
            if (!limiter.allow(key)) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"detail\":\"trop de tentatives\"}");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
