package com.location.canaux;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CanalSignatureStatutTest {
    @Test
    void leStatutExposeLaSignature() {
        var statut = new CanalMockService("MOCK", "MOCK", "MOCK", "LIVE").statut();
        assertEquals("LIVE", statut.get("signature"));
    }
}
