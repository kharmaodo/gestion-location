package com.location.locataires.service;

import com.location.locataires.dto.DocumentMetaRequest;
import com.location.locataires.dto.DocumentResponse;
import com.location.locataires.dto.DossierRequest;
import com.location.locataires.dto.DossierResponse;
import com.location.locataires.dto.KycDecisionRequest;
import com.location.locataires.entity.DocumentEntity;
import com.location.locataires.entity.DossierEntity;
import com.location.locataires.repository.DocumentRepository;
import com.location.locataires.repository.DossierRepository;
import com.location.shared.exception.ApiException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DossierService {
    private static final Set<String> KYC = Set.of("EN_ATTENTE", "VALIDE", "REJETE");
    private static final Set<String> DOC_TYPES = Set.of("CNI", "PASSEPORT", "JUSTIFICATIF_DOMICILE", "PHOTO", "AUTRE");
    private final DossierRepository dossiers;
    private final DocumentRepository documents;
    private final Path uploadRoot = Path.of("data", "uploads", "kyc");

    public DossierService(DossierRepository dossiers, DocumentRepository documents) {
        this.dossiers = dossiers;
        this.documents = documents;
    }

    @Transactional(readOnly = true)
    public List<DossierResponse> lister(UUID proprietaireId) {
        return dossiers.findByProprietaireIdOrderByMajLeDesc(proprietaireId).stream()
                .map(d -> toDto(d, false))
                .toList();
    }

    @Transactional(readOnly = true)
    public DossierResponse detail(UUID proprietaireId, UUID id) {
        return toDto(owned(proprietaireId, id), true);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> listerDocuments(UUID proprietaireId, UUID dossierId) {
        owned(proprietaireId, dossierId);
        return documents.findByDossierIdOrderByCreeLeDesc(dossierId).stream().map(this::toDoc).toList();
    }

    @Transactional
    public DossierResponse creer(UUID proprietaireId, DossierRequest req) {
        DossierEntity e = new DossierEntity();
        e.setId(UUID.randomUUID());
        e.setProprietaireId(proprietaireId);
        apply(e, req);
        dossiers.save(e);
        return toDto(e, true);
    }

    @Transactional
    public DossierResponse modifier(UUID proprietaireId, UUID id, DossierRequest req) {
        DossierEntity e = owned(proprietaireId, id);
        apply(e, req);
        e.setMajLe(Instant.now());
        dossiers.save(e);
        return toDto(e, true);
    }

    @Transactional
    public DossierResponse deciderKyc(UUID proprietaireId, UUID id, KycDecisionRequest req) {
        String statut = req.statut().toUpperCase();
        if (!KYC.contains(statut) || "EN_ATTENTE".equals(statut)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "decision KYC invalide");
        }
        DossierEntity e = owned(proprietaireId, id);
        e.setKycStatut(statut);
        e.setKycCommentaire(req.commentaire());
        e.setMajLe(Instant.now());
        dossiers.save(e);
        return toDto(e, true);
    }

    @Transactional
    public DocumentResponse ajouterDocument(UUID proprietaireId, UUID dossierId, String type, MultipartFile file) {
        owned(proprietaireId, dossierId);
        String t = type == null ? "AUTRE" : type.toUpperCase();
        if (!DOC_TYPES.contains(t)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "type de document invalide");
        }
        if (file == null || file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "fichier requis");
        }
        UUID docId = UUID.randomUUID();
        try {
            Files.createDirectories(uploadRoot);
            String original = file.getOriginalFilename() == null ? "piece" : file.getOriginalFilename();
            Path dest = uploadRoot.resolve(docId + "-" + original.replaceAll("[^a-zA-Z0-9._-]", "_"));
            file.transferTo(dest.toAbsolutePath());
            DocumentEntity d = new DocumentEntity();
            d.setId(docId);
            d.setDossierId(dossierId);
            d.setType(t);
            d.setNomFichier(original);
            d.setChemin(dest.toString());
            d.setMime(file.getContentType());
            documents.save(d);
            return toDoc(d);
        } catch (IOException ex) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "echec enregistrement fichier");
        }
    }

    @Transactional
    public DocumentResponse ajouterMeta(UUID proprietaireId, UUID dossierId, DocumentMetaRequest req) {
        owned(proprietaireId, dossierId);
        String t = req.type().toUpperCase();
        if (!DOC_TYPES.contains(t)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "type de document invalide");
        }
        DocumentEntity d = new DocumentEntity();
        d.setId(UUID.randomUUID());
        d.setDossierId(dossierId);
        d.setType(t);
        d.setNomFichier(req.nomFichier());
        d.setChemin(req.chemin());
        d.setMime(req.mime() == null ? "application/octet-stream" : req.mime());
        documents.save(d);
        return toDoc(d);
    }

    private void apply(DossierEntity e, DossierRequest req) {
        e.setNom(req.nom());
        e.setPrenom(req.prenom());
        e.setTelephone(req.telephone());
        e.setEmail(req.email());
        e.setPieceType(req.pieceType());
        e.setPieceNumero(req.pieceNumero());
    }

    private DossierEntity owned(UUID proprietaireId, UUID id) {
        return dossiers.findByIdAndProprietaireId(id, proprietaireId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Dossier introuvable"));
    }

    private DossierResponse toDto(DossierEntity e, boolean withDocs) {
        List<DocumentResponse> docs = withDocs
                ? documents.findByDossierIdOrderByCreeLeDesc(e.getId()).stream().map(this::toDoc).toList()
                : List.of();
        return new DossierResponse(
                e.getId(), e.getPrenom(), e.getNom(), e.getTelephone(), e.getEmail(),
                e.getPieceType(), e.getPieceNumero(), e.getKycStatut(), e.getKycCommentaire(), docs);
    }

    private DocumentResponse toDoc(DocumentEntity d) {
        return new DocumentResponse(
                d.getId(), d.getType(), d.getNomFichier(), d.getChemin(), d.getMime(), d.getKycStatut(), d.getCreeLe());
    }
}
