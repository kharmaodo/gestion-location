package com.location.avis.service;

import com.location.avis.dto.AvisRequest;
import com.location.avis.dto.AvisResponse;
import com.location.avis.entity.AvisEntity;
import com.location.avis.repository.AvisRepository;
import com.location.biens.repository.UniteLocativeRepository;
import com.location.shared.exception.ApiException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AvisService {
    private final AvisRepository avis;
    private final UniteLocativeRepository unites;

    public AvisService(AvisRepository avis, UniteLocativeRepository unites) {
        this.avis = avis;
        this.unites = unites;
    }

    @Transactional(readOnly = true)
    public List<AvisResponse> listerUnite(UUID uniteId) {
        return avis.findByCibleUniteIdOrderByCreeLeDesc(uniteId).stream().map(this::toDto).toList();
    }

    @Transactional
    public AvisResponse publier(UUID auteurId, AvisRequest req) {
        unites.findById(req.cibleUniteId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Unite introuvable"));
        if (avis.findByAuteurIdAndCibleUniteId(auteurId, req.cibleUniteId()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "avis deja depose sur cette unite");
        }
        AvisEntity e = new AvisEntity();
        e.setId(UUID.randomUUID());
        e.setAuteurId(auteurId);
        e.setCibleUniteId(req.cibleUniteId());
        e.setNote(req.note());
        e.setCommentaire(req.commentaire());
        avis.save(e);
        return toDto(e);
    }

    private AvisResponse toDto(AvisEntity e) {
        return new AvisResponse(
                e.getId(), e.getAuteurId(), e.getCibleUniteId(), e.getNote(), e.getCommentaire(), e.getCreeLe());
    }
}
