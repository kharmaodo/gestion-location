package com.location.messagerie.repository;

import com.location.messagerie.entity.MessageEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {
    List<MessageEntity> findByConversationIdOrderByCreeLeAsc(UUID conversationId);
}
