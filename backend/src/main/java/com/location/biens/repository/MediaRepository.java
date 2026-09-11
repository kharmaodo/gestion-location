package com.location.biens.repository;

import com.location.biens.entity.MediaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<MediaEntity, UUID> {
    List<MediaEntity> findByUniteIdOrderByPositionAsc(UUID uniteId);
    long countByUniteId(UUID uniteId);
    long countByUniteIdAndType(UUID uniteId, String type);
}
