package com.location.visites.repository;

import com.location.visites.entity.VisiteEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisiteRepository extends JpaRepository<VisiteEntity, UUID> {
    List<VisiteEntity> findByProprietaireIdOrderByCreneauAsc(UUID proprietaireId);
    Optional<VisiteEntity> findByIdAndProprietaireId(UUID id, UUID proprietaireId);
}
