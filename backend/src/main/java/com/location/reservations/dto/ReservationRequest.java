package com.location.reservations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record ReservationRequest(
        @NotNull UUID uniteId,
        @NotBlank String nom,
        String prenom,
        String telephone,
        String email,
        @NotNull LocalDate dateDebut,
        @NotNull LocalDate dateFin,
        String message) {}
