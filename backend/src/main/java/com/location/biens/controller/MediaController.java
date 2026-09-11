package com.location.biens.controller;

import com.location.biens.dto.MediaRequest;
import com.location.biens.dto.MediaResponse;
import com.location.biens.service.MediaService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class MediaController {
    private final MediaService service;

    public MediaController(MediaService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/public/annonces/{uniteId}/medias")
    public List<MediaResponse> publics(@PathVariable UUID uniteId) {
        return service.publics(uniteId);
    }

    @PostMapping("/api/v1/unites/{uniteId}/medias")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public MediaResponse ajouter(
            Authentication auth, @PathVariable UUID uniteId, @Valid @RequestBody MediaRequest request) {
        return service.ajouter(UUID.fromString(auth.getName()), uniteId, request);
    }

    @PostMapping("/api/v1/unites/{uniteId}/medias/upload")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public MediaResponse upload(
            Authentication auth,
            @PathVariable UUID uniteId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "PHOTO") String type) {
        return service.upload(UUID.fromString(auth.getName()), uniteId, file, type);
    }
}
