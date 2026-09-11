package com.location.paiements.service;

import com.location.contrats.entity.ContratEntity;
import com.location.contrats.repository.ContratRepository;
import com.location.notifications.service.NotificationService;
import com.location.paiements.dto.EcheanceResponse;
import com.location.paiements.dto.PaiementRequest;
import com.location.paiements.dto.PaiementResponse;
import com.location.paiements.entity.EcheanceEntity;
import com.location.paiements.entity.PaiementEntity;
import com.location.paiements.repository.EcheanceRepository;
import com.location.paiements.repository.PaiementRepository;
import com.location.shared.exception.ApiException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaiementService {
    private final ContratRepository contrats;
    private final EcheanceRepository echeances;
    private final PaiementRepository paiements;
    private final NotificationService notifications;

    public PaiementService(
            ContratRepository contrats,
            EcheanceRepository echeances,
            PaiementRepository paiements,
            NotificationService notifications) {
        this.contrats = contrats;
        this.echeances = echeances;
        this.paiements = paiements;
        this.notifications = notifications;
    }

    @Transactional(readOnly = true)
    public List<EcheanceResponse> lister(UUID proprietaireId) {
        return echeances.findByProprietaireIdOrderByPeriodeDebutDesc(proprietaireId).stream()
                .map(e -> toDto(e, true))
                .toList();
    }

    @Transactional
    public List<EcheanceResponse> generer(UUID proprietaireId) {
        List<ContratEntity> actifs = contrats.findByProprietaireIdOrderByMajLeDesc(proprietaireId).stream()
                .filter(c -> "ACTIF".equals(c.getStatut()))
                .toList();
        for (ContratEntity c : actifs) {
            LocalDate debut = nextDebut(c);
            if (c.getDateFin() != null && !debut.isBefore(c.getDateFin())) {
                continue;
            }
            if (echeances.existsByContratIdAndPeriodeDebut(c.getId(), debut)) {
                continue;
            }
            LocalDate fin = finPeriode(debut, c.getPeriodicite());
            EcheanceEntity e = new EcheanceEntity();
            e.setId(UUID.randomUUID());
            e.setContratId(c.getId());
            e.setProprietaireId(proprietaireId);
            e.setPeriodeDebut(debut);
            e.setPeriodeFin(fin);
            e.setMontant(c.getLoyer());
            echeances.save(e);
        }
        return lister(proprietaireId);
    }

    @Transactional
    public EcheanceResponse encaisser(UUID proprietaireId, UUID echeanceId, PaiementRequest req) {
        EcheanceEntity e = echeances.findByIdAndProprietaireId(echeanceId, proprietaireId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Echeance introuvable"));
        if ("PAYEE".equals(e.getStatut())) {
            throw new ApiException(HttpStatus.CONFLICT, "echeance deja soldée");
        }
        BigDecimal deja = paiements.findByEcheanceIdOrderByPayeLeDesc(e.getId()).stream()
                .map(PaiementEntity::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal reste = e.getMontant().subtract(deja);
        if (req.montant().compareTo(reste) > 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "montant superieur au reste a payer");
        }
        PaiementEntity p = new PaiementEntity();
        p.setId(UUID.randomUUID());
        p.setEcheanceId(e.getId());
        p.setMontant(req.montant());
        p.setMode(req.mode() == null || req.mode().isBlank() ? "ESPECES" : req.mode().toUpperCase());
        p.setReference(req.reference());
        p.setRecuNumero("Q" + Instant.now().getEpochSecond() + "-" + p.getId().toString().substring(0, 8));
        paiements.save(p);
        BigDecimal total = deja.add(req.montant());
        if (total.compareTo(e.getMontant()) >= 0) {
            e.setStatut("PAYEE");
        } else {
            e.setStatut("PARTIEL");
        }
        e.setMajLe(Instant.now());
        echeances.save(e);
        notifications.notifier(
                proprietaireId,
                "PAIEMENT",
                "Paiement " + p.getMontant() + " " + e.getDevise() + " reçu (" + p.getMode() + ") quittance " + p.getRecuNumero());
        return toDto(e, true);
    }

    private LocalDate nextDebut(ContratEntity c) {
        List<EcheanceEntity> existantes = echeances.findByProprietaireIdOrderByPeriodeDebutDesc(c.getProprietaireId())
                .stream().filter(e -> e.getContratId().equals(c.getId())).toList();
        if (existantes.isEmpty()) {
            return c.getDateDebut();
        }
        return existantes.getFirst().getPeriodeFin().plusDays(1);
    }

    private static LocalDate finPeriode(LocalDate debut, String periodicite) {
        return switch (periodicite) {
            case "JOURNALIER" -> debut;
            case "HEBDOMADAIRE" -> debut.plusDays(6);
            default -> debut.plusMonths(1).minusDays(1);
        };
    }

    private EcheanceResponse toDto(EcheanceEntity e, boolean withPaiements) {
        List<PaiementResponse> list = withPaiements
                ? paiements.findByEcheanceIdOrderByPayeLeDesc(e.getId()).stream()
                        .map(p -> new PaiementResponse(
                                p.getId(), p.getMontant(), p.getMode(), p.getReference(), p.getRecuNumero(), p.getPayeLe()))
                        .toList()
                : List.of();
        return new EcheanceResponse(
                e.getId(), e.getContratId(), e.getPeriodeDebut(), e.getPeriodeFin(),
                e.getMontant(), e.getDevise(), e.getStatut(), list);
    }
}
