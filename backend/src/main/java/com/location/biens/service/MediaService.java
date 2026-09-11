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
import com.location.shared.storage.MinioStorageService;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MediaService {
    private static final Set<String> TYPES = Set.of("PHOTO", "VIDEO");
    private final MediaRepository medias;
    private final UniteLocativeRepository unites;
    private final BienImmobilierRepository biens;
    private final MinioStorageService storage;

    public MediaService(
            MediaRepository medias,
            UniteLocativeRepository unites,
            BienImmobilierRepository biens,
            MinioStorageService storage) {
        this.medias = medias;
        this.unites = unites;
        this.biens = biens;
        this.storage = storage;
    }

    @Transactional(readOnly = true)
    public List<MediaResponse> publics(UUID uniteId) {
        return medias.findByUniteIdOrderByPositionAsc(uniteId).stream().map(this::toDto).toList();
    }

    @Transactional
    public MediaResponse ajouter(UUID proprietaireId, UUID uniteId, MediaRequest req) {
        return enregistrer(owned(proprietaireId, uniteId), req.url(), req.type());
    }

    @Transactional
    public MediaResponse upload(UUID proprietaireId, UUID uniteId, MultipartFile file, String type) {
        UniteLocativeEntity u = owned(proprietaireId, uniteId);
        String ext = extension(file.getOriginalFilename());
        String object = uniteId + "/" + UUID.randomUUID() + ext;
        try (InputStream in = file.getInputStream()) {
            String url = storage.upload(object, in, file.getSize(), file.getContentType());
            return enregistrer(u, url, type);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "upload MinIO impossible");
        }
    }

    public long nombrePhotos(UUID uniteId) {
        return medias.countByUniteIdAndType(uniteId, "PHOTO");
    }

    private MediaResponse enregistrer(UniteLocativeEntity u, String url, String typeRaw) {
        String type = typeRaw == null || typeRaw.isBlank() ? "PHOTO" : typeRaw.toUpperCase();
        if (!TYPES.contains(type)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "type media invalide");
        }
        MediaEntity e = new MediaEntity();
        e.setId(UUID.randomUUID());
        e.setUniteId(u.getId());
        e.setUrl(url);
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

    private static String extension(String name) {
        if (name == null || !name.contains(".")) {
            return "";
        }
        return name.substring(name.lastIndexOf('.'));
    }

    private MediaResponse toDto(MediaEntity e) {
        return new MediaResponse(e.getId(), e.getUniteId(), e.getUrl(), e.getType(), e.getPosition());
    }
}
