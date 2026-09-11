package com.location.notifications.service;

import com.location.notifications.dto.NotificationResponse;
import com.location.notifications.entity.NotificationEntity;
import com.location.notifications.repository.NotificationRepository;
import com.location.shared.exception.ApiException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {
    private final NotificationRepository notifications;

    public NotificationService(NotificationRepository notifications) {
        this.notifications = notifications;
    }

    @Transactional
    public void notifier(UUID destinataireId, String type, String message) {
        NotificationEntity e = new NotificationEntity();
        e.setId(UUID.randomUUID());
        e.setDestinataireId(destinataireId);
        e.setType(type);
        e.setMessage(message);
        notifications.save(e);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> lister(UUID userId) {
        return notifications.findByDestinataireIdOrderByCreeLeDesc(userId).stream()
                .map(n -> new NotificationResponse(n.getId(), n.getType(), n.getMessage(), n.isLu(), n.getCreeLe()))
                .toList();
    }

    @Transactional
    public NotificationResponse marquerLu(UUID userId, UUID id) {
        NotificationEntity e = notifications.findByIdAndDestinataireId(id, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Notification introuvable"));
        e.setLu(true);
        notifications.save(e);
        return new NotificationResponse(e.getId(), e.getType(), e.getMessage(), e.isLu(), e.getCreeLe());
    }
}
