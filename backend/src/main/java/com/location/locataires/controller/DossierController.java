package com.location.locataires.controller;

import com.location.locataires.dto.DocumentMetaRequest;
import com.location.locataires.dto.DocumentResponse;
import com.location.locataires.dto.DossierRequest;
import com.location.locataires.dto.DossierResponse;
import com.location.locataires.dto.KycDecisionRequest;
import com.location.locataires.service.DossierService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/locataires")
@PreAuthorize("hasRole('PROPRIETAIRE')")
public class DossierController {

    private final DossierService service;

    public DossierController(DossierService service) {
        this.service = service;
    }

    @GetMapping
    public List<DossierResponse> lister(Authentication auth) {
        return service.lister(uid(auth));
    }

    @GetMapping("/{id}")
    public DossierResponse detail(Authentication auth, @PathVariable UUID id) {
        return service.detail(uid(auth), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DossierResponse creer(Authentication auth, @Valid @RequestBody DossierRequest request) {
        return service.creer(uid(auth), request);
    }

    @PutMapping("/{id}")
    public DossierResponse modifier(Authentication auth, @PathVariable UUID id, @Valid @RequestBody DossierRequest request) {
        return service.modifier(uid(auth), id, request);
    }

    @PostMapping("/{id}/kyc")
    public DossierResponse kyc(Authentication auth, @PathVariable UUID id, @Valid @RequestBody KycDecisionRequest request) {
        return service.deciderKyc(uid(auth), id, request);
    }

    @GetMapping("/{id}/documents")
    public List<DocumentResponse> documents(Authentication auth, @PathVariable UUID id) {
        return service.listerDocuments(uid(auth), id);
    }

    @PostMapping("/{id}/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentResponse document(
            Authentication auth,
            @PathVariable UUID id,
            @RequestParam String type,
            @RequestParam("file") MultipartFile file) {
        return service.ajouterDocument(uid(auth), id, type, file);
    }

    @PostMapping("/{id}/documents/meta")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentResponse documentMeta(
            Authentication auth, @PathVariable UUID id, @Valid @RequestBody DocumentMetaRequest request) {
        return service.ajouterMeta(uid(auth), id, request);
    }

    private static UUID uid(Authentication auth) {
        return UUID.fromString(auth.getName());
    }
}
