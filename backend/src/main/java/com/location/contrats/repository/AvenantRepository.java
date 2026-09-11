package com.location.contrats.repository;

import com.location.contrats.entity.AvenantEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvenantRepository extends JpaRepository<AvenantEntity, UUID> {
    List<AvenantEntity> findByContratIdOrderByCreeLeDesc(UUID contratId);
}
