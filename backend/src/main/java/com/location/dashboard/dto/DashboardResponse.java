package com.location.dashboard.dto;

import java.math.BigDecimal;

public record DashboardResponse(
        long biens,
        long unites,
        long unitesLibres,
        long unitesOccupees,
        long contratsActifs,
        long echeancesAPayer,
        long echeancesPartielles,
        long echeancesPayees,
        BigDecimal encaisse,
        BigDecimal aRecouvrer,
        double tauxOccupation) {}
