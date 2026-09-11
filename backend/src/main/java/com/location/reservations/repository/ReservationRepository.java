package com.location.reservations.repository;

import com.location.reservations.entity.ReservationEntity;
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
              AND r.statut IN ('EN_ATTENTE', 'ACCEPTEE')
              AND r.dateDebut < :fin AND r.dateFin > :debut
            """)
    long countChevauchements(
            @Param("uniteId") UUID uniteId,
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);
}
