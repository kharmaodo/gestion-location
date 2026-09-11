package com.location.locataires.repository;

import com.location.locataires.entity.DocumentEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<DocumentEntity, UUID> {
    List<DocumentEntity> findByDossierIdOrderByCreeLeDesc(UUID dossierId);
}
