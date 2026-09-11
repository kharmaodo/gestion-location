package com.location.paiements.repository;

import com.location.paiements.entity.RelanceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelanceRepository extends JpaRepository<RelanceEntity, UUID> {
    List<RelanceEntity> findByEcheanceIdOrderByEnvoyeeLeDesc(UUID echeanceId);
}
