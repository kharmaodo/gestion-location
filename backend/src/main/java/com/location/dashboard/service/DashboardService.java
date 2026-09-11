package com.location.dashboard.service;

import com.location.biens.entity.BienImmobilierEntity;
import com.location.biens.entity.UniteLocativeEntity;
import com.location.biens.repository.BienImmobilierRepository;
import com.location.biens.repository.UniteLocativeRepository;
import com.location.contrats.repository.ContratRepository;
import com.location.dashboard.dto.DashboardResponse;
import com.location.paiements.entity.EcheanceEntity;
import com.location.paiements.entity.PaiementEntity;
import com.location.paiements.repository.EcheanceRepository;
import com.location.paiements.repository.PaiementRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {
    private final BienImmobilierRepository biens;
    private final UniteLocativeRepository unites;
    private final ContratRepository contrats;
    private final EcheanceRepository echeances;
    private final PaiementRepository paiements;

    public DashboardService(
            BienImmobilierRepository biens,
            UniteLocativeRepository unites,
            ContratRepository contrats,
            EcheanceRepository echeances,
            PaiementRepository paiements) {
        this.biens = biens;
        this.unites = unites;
        this.contrats = contrats;
        this.echeances = echeances;
        this.paiements = paiements;
    }

    @Transactional(readOnly = true)
    public DashboardResponse resume(UUID proprietaireId) {
        List<BienImmobilierEntity> parc = biens.findByProprietaireIdOrderByMajLeDesc(proprietaireId);
        List<UniteLocativeEntity> units = parc.stream()
                .flatMap(b -> unites.findByBienIdOrderByLibelleAsc(b.getId()).stream())
                .toList();
        long libres = units.stream().filter(u -> "LIBRE".equals(u.getStatut())).count();
        long occupees = units.stream().filter(u -> "OCCUPE".equals(u.getStatut())).count();
        long actifs = contrats.findByProprietaireIdOrderByMajLeDesc(proprietaireId).stream()
                .filter(c -> "ACTIF".equals(c.getStatut()))
                .count();
        List<EcheanceEntity> echs = echeances.findByProprietaireIdOrderByPeriodeDebutDesc(proprietaireId);
        long aPayer = echs.stream().filter(e -> "A_PAYER".equals(e.getStatut())).count();
        long partiel = echs.stream().filter(e -> "PARTIEL".equals(e.getStatut())).count();
        long payees = echs.stream().filter(e -> "PAYEE".equals(e.getStatut())).count();
        BigDecimal encaisse = echs.stream()
                .flatMap(e -> paiements.findByEcheanceIdOrderByPayeLeDesc(e.getId()).stream())
                .map(PaiementEntity::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal du = echs.stream().map(EcheanceEntity::getMontant).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal aRecouvrer = du.subtract(encaisse).max(BigDecimal.ZERO);
        double taux = units.isEmpty() ? 0d : (occupees * 100.0) / units.size();
        return new DashboardResponse(
                parc.size(), units.size(), libres, occupees, actifs,
                aPayer, partiel, payees, encaisse, aRecouvrer, Math.round(taux * 10) / 10.0);
    }
}
