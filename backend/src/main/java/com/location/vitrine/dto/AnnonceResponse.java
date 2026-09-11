package com.location.vitrine.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AnnonceResponse(
        UUID uniteId,
        UUID bienId,
        String designationBien,
        String ville,
        String adresse,
        String libelle,
        String type,
        BigDecimal surfaceM2,
        boolean meuble,
        BigDecimal loyer,
        String devise,
        String periodicite,
        String statut) {}
