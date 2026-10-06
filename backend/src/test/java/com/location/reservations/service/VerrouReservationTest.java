package com.location.reservations.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class VerrouReservationTest {
    @Test
    void uneDemandeRecenteBloqueEtUneDemandeExpireeLibere() {
        Instant maintenant = Instant.parse("2026-10-06T10:00:00Z");
        assertTrue(VerrouReservation.tient("EN_ATTENTE", maintenant.minusSeconds(3600), maintenant));
        assertFalse(VerrouReservation.tient("EN_ATTENTE", maintenant.minus(VerrouReservation.DUREE).minusSeconds(1), maintenant));
        assertTrue(VerrouReservation.tient("ACCEPTEE", maintenant.minus(VerrouReservation.DUREE).minusSeconds(1), maintenant));
        assertFalse(VerrouReservation.tient("REFUSEE", maintenant, maintenant));
    }
}
