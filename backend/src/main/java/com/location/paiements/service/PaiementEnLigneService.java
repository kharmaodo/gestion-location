package com.location.paiements.service;

import com.location.paiements.dto.IntentionResponse;
import com.location.paiements.dto.PaiementRequest;
import com.location.paiements.entity.EcheanceEntity;
import com.location.paiements.entity.IntentionPaiementEntity;
import com.location.paiements.repository.EcheanceRepository;
import com.location.paiements.repository.IntentionPaiementRepository;
import com.location.shared.exception.ApiException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaiementEnLigneService {
    private static final Set<String> FOURNISSEURS = Set.of("WAVE", "ORANGE_MONEY", "CARTE");
    private final EcheanceRepository echeances;
    private final IntentionPaiementRepository intentions;
    private final PaiementService paiements;

    public PaiementEnLigneService(
            EcheanceRepository echeances, IntentionPaiementRepository intentions, PaiementService paiements) {
        this.echeances = echeances;
        this.intentions = intentions;
        this.paiements = paiements;
    }

    @Transactional
    public IntentionResponse initier(UUID proprietaireId, UUID echeanceId, String fournisseur) {
        String f = fournisseur.toUpperCase();
        if (!FOURNISSEURS.contains(f)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "fournisseur invalide");
        }
        EcheanceEntity e = echeances.findByIdAndProprietaireId(echeanceId, proprietaireId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Echeance introuvable"));
        if ("PAYEE".equals(e.getStatut())) {
            throw new ApiException(HttpStatus.CONFLICT, "echeance deja payee");
        }
        IntentionPaiementEntity i = new IntentionPaiementEntity();
        i.setId(UUID.randomUUID());
        i.setEcheanceId(e.getId());
        i.setProprietaireId(proprietaireId);
        i.setFournisseur(f);
        i.setMontant(e.getMontant());
        i.setReferenceExterne("MOCK-" + i.getId().toString().substring(0, 8));
        intentions.save(i);
        return toDto(i);
    }

    @Transactional
    public IntentionResponse webhook(UUID intentionId, String statut) {
        IntentionPaiementEntity i = intentions.findById(intentionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Intention introuvable"));
        if (!"EN_ATTENTE".equals(i.getStatut())) {
            throw new ApiException(HttpStatus.CONFLICT, "intention deja traitee");
        }
        String s = statut.toUpperCase();
        if ("REUSSI".equals(s) || "SUCCESS".equals(s)) {
            i.setStatut("REUSSI");
            paiements.encaisser(
                    i.getProprietaireId(),
                    i.getEcheanceId(),
                    new PaiementRequest(i.getMontant(), i.getFournisseur(), i.getReferenceExterne()));
        } else if ("ECHEC".equals(s) || "FAILED".equals(s)) {
            i.setStatut("ECHEC");
        } else {
            throw new ApiException(HttpStatus.BAD_REQUEST, "statut webhook invalide");
        }
        intentions.save(i);
        return toDto(i);
    }

    private IntentionResponse toDto(IntentionPaiementEntity i) {
        return new IntentionResponse(
                i.getId(),
                i.getEcheanceId(),
                i.getFournisseur(),
                i.getMontant(),
                i.getStatut(),
                "/mock-checkout/" + i.getId());
    }
}
