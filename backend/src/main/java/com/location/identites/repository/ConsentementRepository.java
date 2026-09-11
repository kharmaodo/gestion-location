package com.location.identites.repository;

import com.location.identites.entity.ConsentementEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsentementRepository extends JpaRepository<ConsentementEntity, UUID> {}
