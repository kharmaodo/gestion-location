package com.location.reservations.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ReservationResponse(
        UUID id,
        UUID uniteId,
        String nom,
        String prenom,
        String telephone,
        String email,
        LocalDate dateDebut,
        LocalDate dateFin,
        String message,
        String statut,
        Instant creeLe) {}
