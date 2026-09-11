package com.location.biens.repository;

import com.location.biens.entity.UniteLocativeEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniteLocativeRepository extends JpaRepository<UniteLocativeEntity, UUID> {
    List<UniteLocativeEntity> findByBienIdOrderByLibelleAsc(UUID bienId);
    Optional<UniteLocativeEntity> findByIdAndBienId(UUID id, UUID bienId);
    long countByBienId(UUID bienId);
    long countByBienIdAndStatut(UUID bienId, String statut);
}
