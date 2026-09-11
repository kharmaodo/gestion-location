package com.location.paiements.repository;

import com.location.paiements.entity.PaiementEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaiementRepository extends JpaRepository<PaiementEntity, UUID> {
    List<PaiementEntity> findByEcheanceIdOrderByPayeLeDesc(UUID echeanceId);
}
