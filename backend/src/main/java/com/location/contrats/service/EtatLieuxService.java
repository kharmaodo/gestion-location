package com.location.contrats.service;

import com.location.contrats.dto.EtatLieuxRequest;
import com.location.contrats.dto.EtatLieuxResponse;
import com.location.contrats.entity.ContratEntity;
import com.location.contrats.entity.EtatLieuxEntity;
import com.location.contrats.repository.ContratRepository;
import com.location.contrats.repository.EtatLieuxRepository;
import com.location.shared.exception.ApiException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EtatLieuxService {
    private static final Set<String> TYPES = Set.of("ENTREE", "SORTIE");
    private final EtatLieuxRepository etats;
    private final ContratRepository contrats;

    public EtatLieuxService(EtatLieuxRepository etats, ContratRepository contrats) {
        this.etats = etats;
        this.contrats = contrats;
    }

    @Transactional(readOnly = true)
    public List<EtatLieuxResponse> lister(UUID proprietaireId, UUID contratId) {
        ownedContrat(proprietaireId, contratId);
        return etats.findByContratIdOrderByCreeLeAsc(contratId).stream().map(this::toDto).toList();
    }

    @Transactional
    public EtatLieuxResponse creer(UUID proprietaireId, EtatLieuxRequest req) {
        String type = req.type().toUpperCase();
        if (!TYPES.contains(type)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "type invalide");
        }
        ContratEntity c = ownedContrat(proprietaireId, req.contratId());
        if (etats.existsByContratIdAndType(c.getId(), type)) {
            throw new ApiException(HttpStatus.CONFLICT, "etat des lieux deja existant pour ce type");
        }
        EtatLieuxEntity e = new EtatLieuxEntity();
        e.setId(UUID.randomUUID());
        e.setContratId(c.getId());
        e.setProprietaireId(proprietaireId);
        e.setType(type);
        e.setObservations(req.observations());
        e.setCoutReparations(req.coutReparations() == null ? BigDecimal.ZERO : req.coutReparations());
        etats.save(e);
        return toDto(e);
    }

    @Transactional
    public EtatLieuxResponse valider(UUID proprietaireId, UUID id) {
        EtatLieuxEntity e = etats.findByIdAndProprietaireId(id, proprietaireId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Etat des lieux introuvable"));
        e.setStatut("VALIDE");
        etats.save(e);
        return toDto(e);
    }

    private ContratEntity ownedContrat(UUID proprietaireId, UUID contratId) {
        return contrats.findByIdAndProprietaireId(contratId, proprietaireId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Contrat introuvable"));
    }

    private EtatLieuxResponse toDto(EtatLieuxEntity e) {
        return new EtatLieuxResponse(
                e.getId(), e.getContratId(), e.getType(), e.getObservations(),
                e.getCoutReparations(), e.getStatut(), e.getCreeLe());
    }
}
