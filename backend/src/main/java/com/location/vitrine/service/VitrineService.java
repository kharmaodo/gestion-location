package com.location.vitrine.service;

import com.location.biens.entity.BienImmobilierEntity;
import com.location.biens.entity.UniteLocativeEntity;
import com.location.biens.repository.BienImmobilierRepository;
import com.location.biens.repository.UniteLocativeRepository;
import com.location.reservations.repository.ReservationRepository;
import com.location.shared.exception.ApiException;
import com.location.vitrine.dto.AnnonceResponse;
import com.location.vitrine.dto.DisponibiliteResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VitrineService {
    private final UniteLocativeRepository unites;
    private final BienImmobilierRepository biens;
    private final ReservationRepository reservations;

    public VitrineService(
            UniteLocativeRepository unites,
            BienImmobilierRepository biens,
            ReservationRepository reservations) {
        this.unites = unites;
        this.biens = biens;
        this.reservations = reservations;
    }

    @Transactional(readOnly = true)
    public List<AnnonceResponse> catalogue(String ville) {
        return unites.findByPublieTrue().stream()
                .filter(u -> !"OCCUPE".equals(u.getStatut()))
                .map(this::toAnnonce)
                .filter(a -> ville == null || ville.isBlank()
                        || (a.ville() != null && a.ville().toLowerCase().contains(ville.toLowerCase())))
                .toList();
    }

    @Transactional(readOnly = true)
    public AnnonceResponse detail(UUID uniteId) {
        UniteLocativeEntity u = unites.findById(uniteId)
                .filter(UniteLocativeEntity::isPublie)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Annonce introuvable"));
        return toAnnonce(u);
    }

    @Transactional(readOnly = true)
    public List<DisponibiliteResponse> disponibilites(UUID uniteId) {
        unites.findById(uniteId).filter(UniteLocativeEntity::isPublie)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Annonce introuvable"));
        return reservations.findByUniteIdAndStatutIn(uniteId, List.of("EN_ATTENTE", "ACCEPTEE")).stream()
                .map(r -> new DisponibiliteResponse(r.getDateDebut(), r.getDateFin(), r.getStatut()))
                .toList();
    }

    private AnnonceResponse toAnnonce(UniteLocativeEntity u) {
        BienImmobilierEntity b = biens.findById(u.getBienId()).orElseThrow();
        return new AnnonceResponse(
                u.getId(), b.getId(), b.getDesignation(), b.getVille(), b.getAdresse(),
                u.getLibelle(), u.getType(), u.getSurfaceM2(), u.isMeuble(),
                u.getLoyer(), u.getDevise(), u.getPeriodicite(), u.getStatut());
    }
}
