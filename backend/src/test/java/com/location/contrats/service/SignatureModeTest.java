package com.location.contrats.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.location.shared.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class SignatureModeTest {
    @Test
    void mockFournitUneReference() {
        String ref = new SignatureMode("MOCK").reference();
        assertTrue(ref.startsWith("mock:"));
    }

    @Test
    void liveNestPasBranche() {
        ApiException ex = assertThrows(ApiException.class, () -> new SignatureMode("LIVE").reference());
        assertEquals(HttpStatus.NOT_IMPLEMENTED, ex.getStatus());
    }

    @Test
    void offRefuse() {
        ApiException ex = assertThrows(ApiException.class, () -> new SignatureMode("OFF").reference());
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }
}
