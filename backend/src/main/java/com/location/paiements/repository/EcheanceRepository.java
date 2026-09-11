package com.location.paiements.repository;

import com.location.paiements.entity.EcheanceEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EcheanceRepository extends JpaRepository<EcheanceEntity, UUID> {
    List<EcheanceEntity> findByProprietaireIdOrderByPeriodeDebutDesc(UUID proprietaireId);
    Optional<EcheanceEntity> findByIdAndProprietaireId(UUID id, UUID proprietaireId);
    boolean existsByContratIdAndPeriodeDebut(UUID contratId, LocalDate periodeDebut);
}
