package com.location.contrats.service;

import com.location.contrats.dto.CautionSimulationRequest;
import com.location.contrats.dto.CautionSimulationResponse;
import com.location.shared.exception.ApiException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CautionService {
    public static final int PLAFOND_MOIS = 3;
    private static final Set<String> PERIODICITES = Set.of("JOURNALIER", "HEBDOMADAIRE", "MENSUEL");

    public CautionSimulationResponse simuler(CautionSimulationRequest req) {
        String per = req.periodicite() == null || req.periodicite().isBlank() ? "MENSUEL" : req.periodicite().toUpperCase();
        if (!PERIODICITES.contains(per)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "periodicite invalide");
        }
        int demandes = req.mois() == null || req.mois() < 1 ? 1 : req.mois();
        int retenus = Math.min(demandes, PLAFOND_MOIS);
        BigDecimal mensuel = equivalentMensuel(req.loyer(), per);
        BigDecimal plafond = mensuel.multiply(BigDecimal.valueOf(PLAFOND_MOIS));
        BigDecimal calculee = mensuel.multiply(BigDecimal.valueOf(retenus));
        return new CautionSimulationResponse(
                req.loyer(),
                per,
                mensuel,
                demandes,
                retenus,
                plafond,
                calculee,
                "XOF",
                "plafond " + PLAFOND_MOIS + " mois de loyer equivalent");
    }

    public void assertCautionSousPlafond(BigDecimal caution, BigDecimal loyer, String periodicite) {
        if (caution == null) {
            return;
        }
        if (caution.signum() < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "caution negative");
        }
        String per = periodicite == null ? "MENSUEL" : periodicite;
        BigDecimal plafond = equivalentMensuel(loyer, per).multiply(BigDecimal.valueOf(PLAFOND_MOIS));
        if (caution.compareTo(plafond) > 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "caution superieure au plafond de 3 mois");
        }
    }

    public static BigDecimal equivalentMensuel(BigDecimal loyer, String periodicite) {
        if (loyer == null) {
            return BigDecimal.ZERO;
        }
        return switch (periodicite) {
            case "JOURNALIER" -> loyer.multiply(BigDecimal.valueOf(30));
            case "HEBDOMADAIRE" -> loyer.multiply(BigDecimal.valueOf(4));
            default -> loyer.setScale(2, RoundingMode.HALF_UP);
        };
    }
}
