package com.location.contrats.service;

import com.location.biens.entity.BienImmobilierEntity;
import com.location.biens.entity.UniteLocativeEntity;
import com.location.biens.repository.BienImmobilierRepository;
import com.location.biens.repository.UniteLocativeRepository;
import com.location.contrats.dto.CertificatResponse;
import com.location.contrats.entity.ContratEntity;
import com.location.contrats.repository.ContratRepository;
import com.location.locataires.entity.DossierEntity;
import com.location.locataires.repository.DossierRepository;
import com.location.shared.exception.ApiException;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CertificatService {
    private final ContratRepository contrats;
    private final UniteLocativeRepository unites;
    private final BienImmobilierRepository biens;
    private final DossierRepository dossiers;

    public CertificatService(
            ContratRepository contrats,
            UniteLocativeRepository unites,
            BienImmobilierRepository biens,
            DossierRepository dossiers) {
        this.contrats = contrats;
        this.unites = unites;
        this.biens = biens;
        this.dossiers = dossiers;
    }

    @Transactional(readOnly = true)
    public CertificatResponse emettre(UUID proprietaireId, UUID contratId) {
        ContratEntity c = contrats.findByIdAndProprietaireId(contratId, proprietaireId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Contrat introuvable"));
        if (!"ACTIF".equals(c.getStatut())) {
            throw new ApiException(HttpStatus.CONFLICT, "certificat disponible seulement pour un contrat ACTIF");
        }
        UniteLocativeEntity u = unites.findById(c.getUniteId()).orElse(null);
        BienImmobilierEntity b = u == null ? null : biens.findById(u.getBienId()).orElse(null);
        DossierEntity d = c.getDossierId() == null ? null : dossiers.findById(c.getDossierId()).orElse(null);
        String locataire = d == null ? "locataire" : ((d.getPrenom() == null ? "" : d.getPrenom()) + " " + d.getNom()).trim();
        String bien = b == null ? "bien" : b.getDesignation();
        String unite = u == null ? "unite" : u.getLibelle();
        String texte = "Attestation de location : " + locataire + " occupe " + unite + " (" + bien + ") depuis "
                + c.getDateDebut() + ". Contrat " + c.getId() + ", statut ACTIF.";
        return new CertificatResponse(
                c.getId(), "OCCUPATION", c.getStatut(), locataire, bien, unite,
                c.getDateDebut(), c.getDateFin(), texte, Instant.now());
    }
}
