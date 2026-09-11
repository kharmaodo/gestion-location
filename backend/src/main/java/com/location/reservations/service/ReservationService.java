package com.location.reservations.service;

import com.location.biens.entity.BienImmobilierEntity;
import com.location.biens.entity.UniteLocativeEntity;
import com.location.biens.repository.BienImmobilierRepository;
import com.location.biens.repository.UniteLocativeRepository;
import com.location.reservations.dto.ReservationRequest;
import com.location.reservations.dto.ReservationResponse;
import com.location.reservations.entity.ReservationEntity;
import com.location.reservations.repository.ReservationRepository;
import com.location.shared.exception.ApiException;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {
    private static final Set<String> DECISIONS = Set.of("ACCEPTEE", "REFUSEE");
    private final ReservationRepository reservations;
    private final UniteLocativeRepository unites;
    private final BienImmobilierRepository biens;

    public ReservationService(
            ReservationRepository reservations,
            UniteLocativeRepository unites,
            BienImmobilierRepository biens) {
        this.reservations = reservations;
        this.unites = unites;
        this.biens = biens;
    }

    @Transactional
    public ReservationResponse creer(ReservationRequest req, UUID userId) {
        if (!req.dateFin().isAfter(req.dateDebut())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "dateFin doit etre apres dateDebut");
        }
        UniteLocativeEntity unite = unites.findById(req.uniteId())
                .filter(UniteLocativeEntity::isPublie)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Annonce introuvable"));
        if (reservations.countChevauchements(unite.getId(), req.dateDebut(), req.dateFin()) > 0) {
            throw new ApiException(HttpStatus.CONFLICT, "creneau indisponible");
        }
        BienImmobilierEntity bien = biens.findById(unite.getBienId()).orElseThrow();
        ReservationEntity e = new ReservationEntity();
        e.setId(UUID.randomUUID());
        e.setUniteId(unite.getId());
        e.setProprietaireId(bien.getProprietaireId());
        e.setLocataireUserId(userId);
        e.setNom(req.nom());
        e.setPrenom(req.prenom());
        e.setTelephone(req.telephone());
        e.setEmail(req.email());
        e.setDateDebut(req.dateDebut());
        e.setDateFin(req.dateFin());
        e.setMessage(req.message());
        reservations.save(e);
        return toDto(e);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> listerProprio(UUID proprietaireId) {
        return reservations.findByProprietaireIdOrderByCreeLeDesc(proprietaireId).stream().map(this::toDto).toList();
    }

    @Transactional
    public ReservationResponse decider(UUID proprietaireId, UUID id, String statut) {
        String s = statut.toUpperCase();
        if (!DECISIONS.contains(s)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "statut invalide");
        }
        ReservationEntity e = reservations.findByIdAndProprietaireId(id, proprietaireId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Reservation introuvable"));
        if (!"EN_ATTENTE".equals(e.getStatut())) {
            throw new ApiException(HttpStatus.CONFLICT, "reservation deja traitee");
        }
        e.setStatut(s);
        e.setMajLe(Instant.now());
        reservations.save(e);
        return toDto(e);
    }

    private ReservationResponse toDto(ReservationEntity e) {
        return new ReservationResponse(
                e.getId(), e.getUniteId(), e.getNom(), e.getPrenom(), e.getTelephone(), e.getEmail(),
                e.getDateDebut(), e.getDateFin(), e.getMessage(), e.getStatut(), e.getCreeLe());
    }
}
