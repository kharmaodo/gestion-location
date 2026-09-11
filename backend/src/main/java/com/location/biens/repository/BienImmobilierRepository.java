package com.location.biens.repository;

import com.location.biens.entity.BienImmobilierEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BienImmobilierRepository extends JpaRepository<BienImmobilierEntity, UUID> {
    List<BienImmobilierEntity> findByProprietaireIdOrderByMajLeDesc(UUID proprietaireId);
    Optional<BienImmobilierEntity> findByIdAndProprietaireId(UUID id, UUID proprietaireId);
}
