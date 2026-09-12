package com.location.contrats.repository;

import com.location.contrats.entity.EtatLieuxEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EtatLieuxRepository extends JpaRepository<EtatLieuxEntity, UUID> {
    List<EtatLieuxEntity> findByContratIdOrderByCreeLeAsc(UUID contratId);
    boolean existsByContratIdAndType(UUID contratId, String type);
    Optional<EtatLieuxEntity> findByIdAndProprietaireId(UUID id, UUID proprietaireId);
    Optional<EtatLieuxEntity> findByContratIdAndType(UUID contratId, String type);
}
