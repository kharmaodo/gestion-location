package com.location.contrats.repository;

import com.location.contrats.entity.ContratEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContratRepository extends JpaRepository<ContratEntity, UUID> {
    List<ContratEntity> findByProprietaireIdOrderByMajLeDesc(UUID proprietaireId);
    Optional<ContratEntity> findByIdAndProprietaireId(UUID id, UUID proprietaireId);
    boolean existsByUniteIdAndStatut(UUID uniteId, String statut);
}
