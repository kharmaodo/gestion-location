package com.location.reservations.repository;

import com.location.reservations.entity.ReservationEntity;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<ReservationEntity, UUID> {
    List<ReservationEntity> findByProprietaireIdOrderByCreeLeDesc(UUID proprietaireId);
    List<ReservationEntity> findByUniteIdAndStatutIn(UUID uniteId, List<String> statuts);
    Optional<ReservationEntity> findByIdAndProprietaireId(UUID id, UUID proprietaireId);

    @Query("""
            SELECT COUNT(r) FROM ReservationEntity r
            WHERE r.uniteId = :uniteId
              AND r.dateDebut < :fin AND r.dateFin > :debut
              AND (r.statut = 'ACCEPTEE' OR (r.statut = 'EN_ATTENTE' AND r.creeLe > :seuil))
            """)
    long countChevauchements(
            @Param("uniteId") UUID uniteId,
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin,
            @Param("seuil") Instant seuil);
}
