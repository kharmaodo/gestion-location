package com.location.litiges.service;

import com.location.contrats.entity.ContratEntity;
import com.location.contrats.repository.ContratRepository;
import com.location.litiges.dto.LitigeDecisionRequest;
import com.location.litiges.dto.LitigeRequest;
import com.location.litiges.dto.LitigeResponse;
import com.location.litiges.entity.LitigeEntity;
import com.location.litiges.repository.LitigeRepository;
import com.location.locataires.entity.DossierEntity;
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
public class LitigeService {
    private static final Set<String> MOTIFS = Set.of("CAUTION", "DEGATS", "IMPAYE", "RESILIATION", "AUTRE");
    private static final Set<String> DECISIONS = Set.of("EN_COURS", "RESOLU", "REJETE");
    private final LitigeRepository litiges;
    private final ContratRepository contrats;
    private final DossierRepository dossiers;

    public LitigeService(LitigeRepository litiges, ContratRepository contrats, DossierRepository dossiers) {
        this.litiges = litiges;
        this.contrats = contrats;
        this.dossiers = dossiers;
    }

    @Transactional(readOnly = true)
    public List<LitigeResponse> lister(UUID userId, String authorities) {
        if (authorities != null && authorities.contains("PROPRIETAIRE")) {
            return litiges.findByProprietaireIdOrderByMajLeDesc(userId).stream().map(this::toDto).toList();
        }
        List<UUID> dossierIds = dossiers.findByUtilisateurId(userId).stream().map(DossierEntity::getId).toList();
        if (dossierIds.isEmpty()) {
            return List.of();
        }
        List<UUID> contratIds = contrats.findByDossierIdIn(dossierIds).stream().map(ContratEntity::getId).toList();
        if (contratIds.isEmpty()) {
            return List.of();
        }
        return litiges.findByContratIdInOrderByMajLeDesc(contratIds).stream().map(this::toDto).toList();
    }

    @Transactional
    public LitigeResponse ouvrir(UUID auteurId, LitigeRequest req) {
        String motif = req.motif().toUpperCase();
        if (!MOTIFS.contains(motif)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "motif invalide");
        }
        ContratEntity c = contrats.findById(req.contratId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Contrat introuvable"));
        boolean proprio = c.getProprietaireId().equals(auteurId);
        boolean locataire = c.getDossierId() != null && dossiers.findById(c.getDossierId())
                .map(DossierEntity::getUtilisateurId)
                .filter(auteurId::equals)
                .isPresent();
        if (!proprio && !locataire) {
            throw new ApiException(HttpStatus.FORBIDDEN, "contrat hors perimetre");
        }
        LitigeEntity e = new LitigeEntity();
        e.setId(UUID.randomUUID());
        e.setContratId(c.getId());
        e.setProprietaireId(c.getProprietaireId());
        e.setAuteurId(auteurId);
        e.setMotif(motif);
        e.setDescription(req.description());
        litiges.save(e);
        return toDto(e);
    }

    @Transactional
    public LitigeResponse decider(UUID proprietaireId, UUID id, LitigeDecisionRequest req) {
        String statut = req.statut().toUpperCase();
        if (!DECISIONS.contains(statut)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "statut invalide");
        }
        LitigeEntity e = litiges.findByIdAndProprietaireId(id, proprietaireId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Litige introuvable"));
        e.setStatut(statut);
        e.setDecision(req.decision());
        e.setMajLe(Instant.now());
        litiges.save(e);
        return toDto(e);
    }

    private LitigeResponse toDto(LitigeEntity e) {
        return new LitigeResponse(
                e.getId(), e.getContratId(), e.getAuteurId(), e.getMotif(), e.getDescription(),
                e.getStatut(), e.getDecision(), e.getCreeLe());
    }
}
