package com.location.contrats.service;

import com.location.biens.entity.BienImmobilierEntity;
import com.location.biens.entity.UniteLocativeEntity;
import com.location.biens.repository.BienImmobilierRepository;
import com.location.biens.repository.UniteLocativeRepository;
import com.location.contrats.dto.AvenantRequest;
import com.location.contrats.dto.AvenantResponse;
import com.location.contrats.dto.ContratRequest;
import com.location.contrats.dto.ContratResponse;
import com.location.contrats.entity.AvenantEntity;
import com.location.contrats.entity.ContratEntity;
import com.location.contrats.repository.AvenantRepository;
import com.location.contrats.repository.ContratRepository;
import com.location.locataires.repository.DossierRepository;
import com.location.shared.exception.ApiException;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContratService {
    private static final Set<String> PERIODICITES = Set.of("JOURNALIER", "HEBDOMADAIRE", "MENSUEL");
    private final ContratRepository contrats;
    private final AvenantRepository avenants;
    private final UniteLocativeRepository unites;
    private final BienImmobilierRepository biens;
    private final DossierRepository dossiers;

    public ContratService(
            ContratRepository contrats,
            AvenantRepository avenants,
            UniteLocativeRepository unites,
            BienImmobilierRepository biens,
            DossierRepository dossiers) {
        this.contrats = contrats;
        this.avenants = avenants;
        this.unites = unites;
        this.biens = biens;
        this.dossiers = dossiers;
    }

    @Transactional(readOnly = true)
    public List<ContratResponse> lister(UUID proprietaireId) {
        return contrats.findByProprietaireIdOrderByMajLeDesc(proprietaireId).stream()
                .map(c -> toDto(c, false))
                .toList();
    }

    @Transactional(readOnly = true)
    public ContratResponse detail(UUID proprietaireId, UUID id) {
        return toDto(owned(proprietaireId, id), true);
    }

    @Transactional
    public ContratResponse creer(UUID proprietaireId, ContratRequest req) {
        UniteLocativeEntity unite = unites.findById(req.uniteId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Unite introuvable"));
        BienImmobilierEntity bien = biens.findById(unite.getBienId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Bien introuvable"));
        if (!bien.getProprietaireId().equals(proprietaireId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Unite introuvable");
        }
        if (req.dossierId() != null) {
            dossiers.findByIdAndProprietaireId(req.dossierId(), proprietaireId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Dossier introuvable"));
        }
        if (contrats.existsByUniteIdAndStatut(unite.getId(), "ACTIF")) {
            throw new ApiException(HttpStatus.CONFLICT, "un contrat actif existe deja sur cette unite");
        }
        ContratEntity e = new ContratEntity();
        e.setId(UUID.randomUUID());
        e.setProprietaireId(proprietaireId);
        e.setUniteId(unite.getId());
        e.setDossierId(req.dossierId());
        e.setReservationId(req.reservationId());
        e.setDateDebut(req.dateDebut());
        e.setDateFin(req.dateFin());
        e.setLoyer(req.loyer() != null ? req.loyer() : unite.getLoyer());
        e.setPeriodicite(normalizePer(req.periodicite() != null ? req.periodicite() : unite.getPeriodicite()));
        e.setJourEcheance(req.jourEcheance() != null ? req.jourEcheance() : unite.getJourEcheance());
        e.setCaution(req.caution());
        contrats.save(e);
        return toDto(e, true);
    }

    @Transactional
    public ContratResponse activer(UUID proprietaireId, UUID id) {
        ContratEntity e = owned(proprietaireId, id);
        if (!"BROUILLON".equals(e.getStatut())) {
            throw new ApiException(HttpStatus.CONFLICT, "contrat non activable");
        }
        if (contrats.existsByUniteIdAndStatut(e.getUniteId(), "ACTIF")) {
            throw new ApiException(HttpStatus.CONFLICT, "un contrat actif existe deja sur cette unite");
        }
        e.setStatut("ACTIF");
        e.setMajLe(Instant.now());
        contrats.save(e);
        unites.findById(e.getUniteId()).ifPresent(u -> {
            u.setStatut("OCCUPE");
            u.setPublie(false);
            u.setMajLe(Instant.now());
            unites.save(u);
        });
        return toDto(e, true);
    }

    @Transactional
    public ContratResponse resilier(UUID proprietaireId, UUID id) {
        ContratEntity e = owned(proprietaireId, id);
        if (!"ACTIF".equals(e.getStatut())) {
            throw new ApiException(HttpStatus.CONFLICT, "seul un contrat actif peut etre resilie");
        }
        e.setStatut("RESILIE");
        e.setMajLe(Instant.now());
        contrats.save(e);
        unites.findById(e.getUniteId()).ifPresent(u -> {
            u.setStatut("LIBRE");
            u.setMajLe(Instant.now());
            unites.save(u);
        });
        return toDto(e, true);
    }

    @Transactional
    public ContratResponse avenant(UUID proprietaireId, UUID id, AvenantRequest req) {
        ContratEntity e = owned(proprietaireId, id);
        if (!"ACTIF".equals(e.getStatut())) {
            throw new ApiException(HttpStatus.CONFLICT, "avenant possible seulement sur contrat actif");
        }
        AvenantEntity a = new AvenantEntity();
        a.setId(UUID.randomUUID());
        a.setContratId(e.getId());
        a.setMotif(req.motif());
        a.setDateEffet(req.dateEffet());
        if (req.periodicite() != null && !req.periodicite().isBlank()) {
            a.setPeriodicite(normalizePer(req.periodicite()));
            e.setPeriodicite(a.getPeriodicite());
        }
        if (req.loyer() != null) {
            a.setLoyer(req.loyer());
            e.setLoyer(req.loyer());
        }
        e.setMajLe(Instant.now());
        avenants.save(a);
        contrats.save(e);
        unites.findById(e.getUniteId()).ifPresent(u -> {
            if (a.getPeriodicite() != null) {
                u.setPeriodicite(a.getPeriodicite());
            }
            if (a.getLoyer() != null) {
                u.setLoyer(a.getLoyer());
            }
            u.setMajLe(Instant.now());
            unites.save(u);
        });
        return toDto(e, true);
    }

    private String normalizePer(String value) {
        String p = value.toUpperCase();
        if (!PERIODICITES.contains(p)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "periodicite invalide");
        }
        return p;
    }

    private ContratEntity owned(UUID proprietaireId, UUID id) {
        return contrats.findByIdAndProprietaireId(id, proprietaireId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Contrat introuvable"));
    }

    private ContratResponse toDto(ContratEntity e, boolean withAvenants) {
        List<AvenantResponse> list = withAvenants
                ? avenants.findByContratIdOrderByCreeLeDesc(e.getId()).stream()
                        .map(a -> new AvenantResponse(
                                a.getId(), a.getMotif(), a.getPeriodicite(), a.getLoyer(), a.getDateEffet(), a.getCreeLe()))
                        .toList()
                : List.of();
        return new ContratResponse(
                e.getId(), e.getUniteId(), e.getDossierId(), e.getReservationId(),
                e.getDateDebut(), e.getDateFin(), e.getLoyer(), e.getDevise(), e.getPeriodicite(),
                e.getJourEcheance(), e.getCaution(), e.getStatut(), list);
    }
}
