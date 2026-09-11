package com.location.contrats.repository;

import com.location.contrats.entity.SignatureEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignatureRepository extends JpaRepository<SignatureEntity, UUID> {
    List<SignatureEntity> findByContratIdOrderByCreeLeAsc(UUID contratId);
    Optional<SignatureEntity> findByTokenHash(String tokenHash);
    long countByStatut(String statut);
}
