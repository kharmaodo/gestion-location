package com.location.contrats.service;

import com.location.contrats.dto.RestitutionCautionResponse;
import com.location.contrats.entity.ContratEntity;
import com.location.contrats.entity.EtatLieuxEntity;
import com.location.contrats.repository.ContratRepository;
import com.location.contrats.repository.EtatLieuxRepository;
import com.location.shared.exception.ApiException;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestitutionCautionService {
    private final ContratRepository contrats;
    private final EtatLieuxRepository etats;

    public RestitutionCautionService(ContratRepository contrats, EtatLieuxRepository etats) {
        this.contrats = contrats;
        this.etats = etats;
    }

    @Transactional(readOnly = true)
    public RestitutionCautionResponse calculer(UUID proprietaireId, UUID contratId) {
        ContratEntity c = contrats.findByIdAndProprietaireId(contratId, proprietaireId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Contrat introuvable"));
        BigDecimal caution = c.getCaution() == null ? BigDecimal.ZERO : c.getCaution();
        EtatLieuxEntity sortie = etats.findByContratIdAndType(c.getId(), "SORTIE").orElse(null);
        boolean valide = sortie != null && "VALIDE".equals(sortie.getStatut());
        BigDecimal reparations = (sortie == null || sortie.getCoutReparations() == null)
                ? BigDecimal.ZERO
                : sortie.getCoutReparations();
        if (!valide) {
            return new RestitutionCautionResponse(
                    c.getId(), caution, reparations, BigDecimal.ZERO, null, false, c.getDevise());
        }
        BigDecimal retenu = reparations.min(caution).max(BigDecimal.ZERO);
        BigDecimal restitue = caution.subtract(retenu).max(BigDecimal.ZERO);
        return new RestitutionCautionResponse(
                c.getId(), caution, reparations, retenu, restitue, true, c.getDevise());
    }
}
