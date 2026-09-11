package com.location.litiges.repository;

import com.location.litiges.entity.LitigeEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LitigeRepository extends JpaRepository<LitigeEntity, UUID> {
    List<LitigeEntity> findByProprietaireIdOrderByMajLeDesc(UUID proprietaireId);
    Optional<LitigeEntity> findByIdAndProprietaireId(UUID id, UUID proprietaireId);
}
