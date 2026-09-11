package com.location.contrats.service;

import com.location.contrats.dto.ContactReveleResponse;
import com.location.contrats.dto.ContactReveleResponse.ContactPartie;
import com.location.contrats.entity.ContratEntity;
import com.location.contrats.entity.SignatureEntity;
import com.location.contrats.repository.ContratRepository;
import com.location.contrats.repository.SignatureRepository;
import com.location.identites.entity.UtilisateurEntity;
import com.location.identites.repository.UtilisateurRepository;
import com.location.locataires.entity.DossierEntity;
import com.location.locataires.repository.DossierRepository;
import com.location.shared.exception.ApiException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactReveleService {
    private final ContratRepository contrats;
    private final SignatureRepository signatures;
    private final UtilisateurRepository utilisateurs;
    private final DossierRepository dossiers;

    public ContactReveleService(
            ContratRepository contrats,
            SignatureRepository signatures,
            UtilisateurRepository utilisateurs,
            DossierRepository dossiers) {
        this.contrats = contrats;
        this.signatures = signatures;
        this.utilisateurs = utilisateurs;
        this.dossiers = dossiers;
    }

    @Transactional(readOnly = true)
    public ContactReveleResponse contacts(UUID proprietaireId, UUID contratId) {
        ContratEntity c = contrats.findByIdAndProprietaireId(contratId, proprietaireId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Contrat introuvable"));
        List<SignatureEntity> sigs = signatures.findByContratIdOrderByCreeLeAsc(c.getId());
        boolean proprioSigne = sigs.stream().anyMatch(s -> "PROPRIETAIRE".equals(s.getRoleSignataire()) && "SIGNE".equals(s.getStatut()));
        boolean locSigne = sigs.stream().anyMatch(s -> "LOCATAIRE".equals(s.getRoleSignataire()) && "SIGNE".equals(s.getStatut()));
        if (!proprioSigne || !locSigne) {
            return new ContactReveleResponse(
                    false,
                    "Les deux parties doivent avoir signe",
                    new ContactPartie(null, masque(null), masque(null)),
                    new ContactPartie(null, masque(null), masque(null)));
        }
        UtilisateurEntity proprio = utilisateurs.findById(c.getProprietaireId()).orElse(null);
        DossierEntity dossier = c.getDossierId() == null ? null : dossiers.findById(c.getDossierId()).orElse(null);
        return new ContactReveleResponse(
                true,
                null,
                new ContactPartie(
                        nom(proprio == null ? null : proprio.getPrenom(), proprio == null ? null : proprio.getNom()),
                        proprio == null ? null : proprio.getEmail(),
                        proprio == null ? null : proprio.getTelephone()),
                new ContactPartie(
                        dossier == null ? null : nom(dossier.getPrenom(), dossier.getNom()),
                        dossier == null ? null : dossier.getEmail(),
                        dossier == null ? null : dossier.getTelephone()));
    }

    private static String nom(String prenom, String nom) {
        if (prenom == null && nom == null) {
            return null;
        }
        return ((prenom == null ? "" : prenom) + " " + (nom == null ? "" : nom)).trim();
    }

    private static String masque(String ignored) {
        return "****";
    }
}
