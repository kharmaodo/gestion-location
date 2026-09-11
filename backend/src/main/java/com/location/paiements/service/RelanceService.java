package com.location.paiements.service;

import com.location.paiements.dto.RelanceResponse;
import com.location.paiements.entity.EcheanceEntity;
import com.location.paiements.entity.RelanceEntity;
import com.location.paiements.repository.EcheanceRepository;
import com.location.paiements.repository.RelanceRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RelanceService {
    private static final Logger log = LoggerFactory.getLogger(RelanceService.class);
    private final EcheanceRepository echeances;
    private final RelanceRepository relances;

    public RelanceService(EcheanceRepository echeances, RelanceRepository relances) {
        this.echeances = echeances;
        this.relances = relances;
    }

    @Transactional
    public List<RelanceResponse> declencher(UUID proprietaireId) {
        List<RelanceResponse> out = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (EcheanceEntity e : echeances.findByProprietaireIdOrderByPeriodeDebutDesc(proprietaireId)) {
            if ("PAYEE".equals(e.getStatut())) {
                continue;
            }
            boolean due = !e.getPeriodeFin().isAfter(today) || "A_PAYER".equals(e.getStatut()) || "PARTIEL".equals(e.getStatut());
            if (!due) {
                continue;
            }
            RelanceEntity r = new RelanceEntity();
            r.setId(UUID.randomUUID());
            r.setEcheanceId(e.getId());
            r.setCanal("EMAIL");
            r.setMessage("Relance loyer " + e.getMontant() + " " + e.getDevise() + " periode " + e.getPeriodeDebut());
            relances.save(r);
            log.info("Relance {} pour echeance {}", r.getId(), e.getId());
            out.add(new RelanceResponse(r.getId(), r.getEcheanceId(), r.getCanal(), r.getMessage(), r.getEnvoyeeLe()));
        }
        return out;
    }

    @Transactional(readOnly = true)
    public List<RelanceResponse> lister(UUID proprietaireId) {
        return echeances.findByProprietaireIdOrderByPeriodeDebutDesc(proprietaireId).stream()
                .flatMap(e -> relances.findByEcheanceIdOrderByEnvoyeeLeDesc(e.getId()).stream())
                .map(r -> new RelanceResponse(r.getId(), r.getEcheanceId(), r.getCanal(), r.getMessage(), r.getEnvoyeeLe()))
                .toList();
    }
}
