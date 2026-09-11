package com.location.locataires.repository;

import com.location.locataires.entity.DossierEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DossierRepository extends JpaRepository<DossierEntity, UUID> {
    List<DossierEntity> findByProprietaireIdOrderByMajLeDesc(UUID proprietaireId);
    Optional<DossierEntity> findByIdAndProprietaireId(UUID id, UUID proprietaireId);
}
