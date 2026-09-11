package com.location.contrats.service;

import com.location.contrats.dto.SignatureInviteRequest;
import com.location.contrats.dto.SignatureResponse;
import com.location.contrats.entity.ContratEntity;
import com.location.contrats.entity.SignatureEntity;
import com.location.contrats.repository.ContratRepository;
import com.location.contrats.repository.SignatureRepository;
import com.location.shared.exception.ApiException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignatureService {
    private static final Set<String> ROLES = Set.of("PROPRIETAIRE", "LOCATAIRE");
    private final SignatureRepository signatures;
    private final ContratRepository contrats;

    public SignatureService(SignatureRepository signatures, ContratRepository contrats) {
        this.signatures = signatures;
        this.contrats = contrats;
    }

    @Transactional
    public SignatureResponse inviter(UUID proprietaireId, SignatureInviteRequest req) {
        String role = req.roleSignataire().toUpperCase();
        if (!ROLES.contains(role)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "role invalide");
        }
        ContratEntity c = contrats.findByIdAndProprietaireId(req.contratId(), proprietaireId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Contrat introuvable"));
        String raw = UUID.randomUUID() + "." + UUID.randomUUID();
        SignatureEntity e = new SignatureEntity();
        e.setId(UUID.randomUUID());
        e.setContratId(c.getId());
        e.setProprietaireId(proprietaireId);
        e.setRoleSignataire(role);
        e.setNomSignataire(req.nomSignataire());
        e.setTokenHash(sha256(raw));
        signatures.save(e);
        return toDto(e, raw);
    }

    @Transactional(readOnly = true)
    public List<SignatureResponse> lister(UUID proprietaireId, UUID contratId) {
        contrats.findByIdAndProprietaireId(contratId, proprietaireId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Contrat introuvable"));
        return signatures.findByContratIdOrderByCreeLeAsc(contratId).stream().map(s -> toDto(s, null)).toList();
    }

    @Transactional
    public SignatureResponse signer(String rawToken) {
        SignatureEntity e = signatures.findByTokenHash(sha256(rawToken))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Lien de signature invalide"));
        if (!"EN_ATTENTE".equals(e.getStatut())) {
            throw new ApiException(HttpStatus.CONFLICT, "deja signe");
        }
        e.setStatut("SIGNE");
        e.setSigneLe(Instant.now());
        signatures.save(e);
        return toDto(e, null);
    }

    private SignatureResponse toDto(SignatureEntity e, String raw) {
        String lien = raw == null ? null : "/api/v1/public/signatures/" + raw;
        return new SignatureResponse(
                e.getId(), e.getContratId(), e.getRoleSignataire(), e.getNomSignataire(), e.getStatut(), e.getSigneLe(), lien);
    }

    private static String sha256(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
