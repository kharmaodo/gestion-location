package com.location.reservations.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class ReservationExpireeTest {
    @Test
    void uneAttenteDePlusDe24hDevientExpiree() {
        Instant maintenant = Instant.parse("2026-10-06T12:00:00Z");
        Instant vieille = maintenant.minus(VerrouReservation.DUREE).minusSeconds(1);
        assertEquals("EXPIREE", VerrouReservation.statutVisible("EN_ATTENTE", vieille, maintenant));
        assertEquals("EN_ATTENTE", VerrouReservation.statutVisible("EN_ATTENTE", maintenant.minusSeconds(60), maintenant));
        assertEquals("ACCEPTEE", VerrouReservation.statutVisible("ACCEPTEE", vieille, maintenant));
    }
}
