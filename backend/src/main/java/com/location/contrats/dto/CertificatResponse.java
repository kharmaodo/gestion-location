package com.location.contrats.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CertificatResponse(
        UUID contratId,
        String type,
        String statutContrat,
        String locataire,
        String bien,
        String unite,
        LocalDate dateDebut,
        LocalDate dateFin,
        String texte,
        Instant emisLe) {}
