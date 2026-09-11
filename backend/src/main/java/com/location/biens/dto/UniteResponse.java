package com.location.biens.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record UniteResponse(
        UUID id,
        UUID bienId,
        String libelle,
        String type,
        BigDecimal surfaceM2,
        boolean meuble,
        BigDecimal loyer,
        String devise,
        String periodicite,
        Integer jourEcheance,
        String statut,
        boolean publie) {}
