package com.location.paiements.repository;

import com.location.paiements.entity.IntentionPaiementEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntentionPaiementRepository extends JpaRepository<IntentionPaiementEntity, UUID> {}
