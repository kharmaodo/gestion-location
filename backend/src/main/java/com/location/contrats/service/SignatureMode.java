package com.location.contrats.service;

import com.location.shared.exception.ApiException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class SignatureMode {
    private final String mode;

    public SignatureMode(@Value("${app.channels.signature:MOCK}") String mode) {
        this.mode = mode == null ? "MOCK" : mode.trim().toUpperCase();
    }

    public String reference() {
        if ("OFF".equals(mode)) {
            throw new ApiException(HttpStatus.CONFLICT, "signature desactivee");
        }
        if ("LIVE".equals(mode)) {
            throw new ApiException(HttpStatus.NOT_IMPLEMENTED, "prestataire de signature non branche");
        }
        return "mock:" + UUID.randomUUID();
    }

    public String mode() {
        return mode;
    }
}
