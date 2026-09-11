package com.location.messagerie.repository;

import com.location.messagerie.entity.ConversationEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConversationRepository extends JpaRepository<ConversationEntity, UUID> {
    @Query("""
            SELECT c FROM ConversationEntity c
            WHERE c.participantA = :uid OR c.participantB = :uid
            ORDER BY c.majLe DESC
            """)
    List<ConversationEntity> findMine(@Param("uid") UUID uid);

    @Query("""
            SELECT c FROM ConversationEntity c
            WHERE ((c.participantA = :a AND c.participantB = :b) OR (c.participantA = :b AND c.participantB = :a))
              AND ((:uniteId IS NULL AND c.uniteId IS NULL) OR c.uniteId = :uniteId)
            """)
    Optional<ConversationEntity> findPair(
            @Param("a") UUID a, @Param("b") UUID b, @Param("uniteId") UUID uniteId);
}
