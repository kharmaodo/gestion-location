package com.location.dashboard.service;

import com.location.dashboard.dto.AlerteResponse;
import com.location.paiements.entity.EcheanceEntity;
import com.location.paiements.repository.EcheanceRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlerteService {
    private final EcheanceRepository echeances;

    public AlerteService(EcheanceRepository echeances) {
        this.echeances = echeances;
    }

    @Transactional(readOnly = true)
    public List<AlerteResponse> lister(UUID proprietaireId) {
        LocalDate today = LocalDate.now();
        List<AlerteResponse> out = new ArrayList<>();
        for (EcheanceEntity e : echeances.findByProprietaireIdOrderByPeriodeDebutDesc(proprietaireId)) {
            if ("PAYEE".equals(e.getStatut())) {
                continue;
            }
            boolean retard = e.getPeriodeFin() != null && e.getPeriodeFin().isBefore(today);
            if (!retard && !"PARTIEL".equals(e.getStatut()) && !"A_PAYER".equals(e.getStatut())) {
                continue;
            }
            String niveau = retard ? "CRITIQUE" : "INFO";
            String type = retard ? "LOYER_RETARD" : "LOYER_A_PAYER";
            String msg = retard
                    ? "Loyer en retard depuis " + e.getPeriodeFin()
                    : "Echeance a payer " + e.getMontant() + " " + e.getDevise();
            out.add(new AlerteResponse(
                    type, niveau, e.getId(), e.getContratId(), e.getPeriodeFin(), e.getStatut(), e.getMontant(), msg));
        }
        return out;
    }
}
