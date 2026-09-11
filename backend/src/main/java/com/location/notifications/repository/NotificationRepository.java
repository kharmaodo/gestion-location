package com.location.notifications.repository;

import com.location.notifications.entity.NotificationEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {
    List<NotificationEntity> findByDestinataireIdOrderByCreeLeDesc(UUID destinataireId);
    Optional<NotificationEntity> findByIdAndDestinataireId(UUID id, UUID destinataireId);
}
