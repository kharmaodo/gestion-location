package com.location.visites.service;

import com.location.biens.entity.BienImmobilierEntity;
import com.location.biens.entity.UniteLocativeEntity;
import com.location.biens.repository.BienImmobilierRepository;
import com.location.biens.repository.UniteLocativeRepository;
import com.location.shared.exception.ApiException;
import com.location.visites.dto.VisiteRequest;
import com.location.visites.dto.VisiteResponse;
import com.location.visites.entity.VisiteEntity;
import com.location.visites.repository.VisiteRepository;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VisiteService {
    private static final Set<String> STATUTS = Set.of("CONFIRMEE", "ANNULEE", "EFFECTUEE");
    private final VisiteRepository visites;
    private final UniteLocativeRepository unites;
    private final BienImmobilierRepository biens;

    public VisiteService(
            VisiteRepository visites, UniteLocativeRepository unites, BienImmobilierRepository biens) {
        this.visites = visites;
        this.unites = unites;
        this.biens = biens;
    }

    @Transactional
    public VisiteResponse demander(VisiteRequest req) {
        UniteLocativeEntity u = unites.findById(req.uniteId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Unite introuvable"));
        if (!Boolean.TRUE.equals(u.getPublie()) || !"LIBRE".equals(u.getStatut())) {
            throw new ApiException(HttpStatus.CONFLICT, "visite impossible sur cette unite");
        }
        if (req.creneau().isBefore(Instant.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "creneau dans le passe");
        }
        BienImmobilierEntity bien = biens.findById(u.getBienId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Bien introuvable"));
        VisiteEntity e = new VisiteEntity();
        e.setId(UUID.randomUUID());
        e.setUniteId(u.getId());
        e.setProprietaireId(bien.getProprietaireId());
        e.setNom(req.nom());
        e.setTelephone(req.telephone());
        e.setEmail(req.email());
        e.setCreneau(req.creneau());
        visites.save(e);
        return toDto(e);
    }

    @Transactional(readOnly = true)
    public List<VisiteResponse> lister(UUID proprietaireId) {
        return visites.findByProprietaireIdOrderByCreneauAsc(proprietaireId).stream().map(this::toDto).toList();
    }

    @Transactional
    public VisiteResponse changerStatut(UUID proprietaireId, UUID id, String statut) {
        String s = statut.toUpperCase();
        if (!STATUTS.contains(s)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "statut invalide");
        }
        VisiteEntity e = visites.findByIdAndProprietaireId(id, proprietaireId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Visite introuvable"));
        e.setStatut(s);
        visites.save(e);
        return toDto(e);
    }

    private VisiteResponse toDto(VisiteEntity e) {
        return new VisiteResponse(
                e.getId(), e.getUniteId(), e.getNom(), e.getTelephone(), e.getEmail(), e.getCreneau(), e.getStatut());
    }
}
