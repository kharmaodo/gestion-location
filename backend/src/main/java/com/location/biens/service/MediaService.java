package com.location.biens.service;

import com.location.biens.dto.MediaRequest;
import com.location.biens.dto.MediaResponse;
import com.location.biens.entity.BienImmobilierEntity;
import com.location.biens.entity.MediaEntity;
import com.location.biens.entity.UniteLocativeEntity;
import com.location.biens.repository.BienImmobilierRepository;
import com.location.biens.repository.MediaRepository;
import com.location.biens.repository.UniteLocativeRepository;
import com.location.shared.exception.ApiException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MediaService {
    private static final Set<String> TYPES = Set.of("PHOTO", "VIDEO");
    private final MediaRepository medias;
    private final UniteLocativeRepository unites;
    private final BienImmobilierRepository biens;

    public MediaService(
            MediaRepository medias, UniteLocativeRepository unites, BienImmobilierRepository biens) {
        this.medias = medias;
        this.unites = unites;
        this.biens = biens;
    }

    @Transactional(readOnly = true)
    public List<MediaResponse> publics(UUID uniteId) {
        return medias.findByUniteIdOrderByPositionAsc(uniteId).stream().map(this::toDto).toList();
    }

    @Transactional
    public MediaResponse ajouter(UUID proprietaireId, UUID uniteId, MediaRequest req) {
        UniteLocativeEntity u = owned(proprietaireId, uniteId);
        String type = req.type() == null || req.type().isBlank() ? "PHOTO" : req.type().toUpperCase();
        if (!TYPES.contains(type)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "type media invalide");
        }
        MediaEntity e = new MediaEntity();
        e.setId(UUID.randomUUID());
        e.setUniteId(u.getId());
        e.setUrl(req.url());
        e.setType(type);
        e.setPosition((int) medias.countByUniteId(u.getId()));
        medias.save(e);
        return toDto(e);
    }

    private UniteLocativeEntity owned(UUID proprietaireId, UUID uniteId) {
        UniteLocativeEntity u = unites.findById(uniteId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Unite introuvable"));
        BienImmobilierEntity b = biens.findById(u.getBienId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Bien introuvable"));
        if (!b.getProprietaireId().equals(proprietaireId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Unite introuvable");
        }
        return u;
    }

    private MediaResponse toDto(MediaEntity e) {
        return new MediaResponse(e.getId(), e.getUniteId(), e.getUrl(), e.getType(), e.getPosition());
    }
}
