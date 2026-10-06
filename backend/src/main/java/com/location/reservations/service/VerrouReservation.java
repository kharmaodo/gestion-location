package com.location.reservations.service;

import java.time.Duration;
import java.time.Instant;

public final class VerrouReservation {
    public static final Duration DUREE = Duration.ofHours(24);

    private VerrouReservation() {}

    public static Instant seuil(Instant maintenant) {
        return maintenant.minus(DUREE);
    }

    public static boolean tient(String statut, Instant creeLe, Instant maintenant) {
        if ("ACCEPTEE".equals(statut)) {
            return true;
        }
        return "EN_ATTENTE".equals(statut) && creeLe != null && creeLe.isAfter(seuil(maintenant));
    }
}
