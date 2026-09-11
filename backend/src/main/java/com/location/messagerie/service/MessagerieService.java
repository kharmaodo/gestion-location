package com.location.messagerie.service;

import com.location.identites.repository.UtilisateurRepository;
import com.location.messagerie.dto.ConversationRequest;
import com.location.messagerie.dto.ConversationResponse;
import com.location.messagerie.dto.MessageResponse;
import com.location.messagerie.entity.ConversationEntity;
import com.location.messagerie.entity.MessageEntity;
import com.location.messagerie.repository.ConversationRepository;
import com.location.messagerie.repository.MessageRepository;
import com.location.shared.exception.ApiException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessagerieService {
    private final ConversationRepository conversations;
    private final MessageRepository messages;
    private final UtilisateurRepository utilisateurs;

    public MessagerieService(
            ConversationRepository conversations,
            MessageRepository messages,
            UtilisateurRepository utilisateurs) {
        this.conversations = conversations;
        this.messages = messages;
        this.utilisateurs = utilisateurs;
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> lister(UUID userId) {
        return conversations.findMine(userId).stream().map(c -> toDto(c, false)).toList();
    }

    @Transactional(readOnly = true)
    public ConversationResponse detail(UUID userId, UUID id) {
        return toDto(owned(userId, id), true);
    }

    @Transactional
    public ConversationResponse ouvrir(UUID userId, ConversationRequest req) {
        if (userId.equals(req.destinataireId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "destinataire invalide");
        }
        utilisateurs.findById(req.destinataireId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "destinataire introuvable"));
        ConversationEntity existing = conversations.findPair(userId, req.destinataireId(), req.uniteId()).orElse(null);
        if (existing != null) {
            return toDto(existing, true);
        }
        ConversationEntity c = new ConversationEntity();
        c.setId(UUID.randomUUID());
        c.setParticipantA(userId);
        c.setParticipantB(req.destinataireId());
        c.setUniteId(req.uniteId());
        conversations.save(c);
        return toDto(c, true);
    }

    @Transactional
    public MessageResponse ecrire(UUID userId, UUID conversationId, String corps) {
        ConversationEntity c = owned(userId, conversationId);
        MessageEntity m = new MessageEntity();
        m.setId(UUID.randomUUID());
        m.setConversationId(c.getId());
        m.setAuteurId(userId);
        m.setCorps(corps);
        messages.save(m);
        c.setMajLe(Instant.now());
        conversations.save(c);
        return new MessageResponse(m.getId(), m.getAuteurId(), m.getCorps(), m.getCreeLe());
    }

    private ConversationEntity owned(UUID userId, UUID id) {
        ConversationEntity c = conversations.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Conversation introuvable"));
        if (!userId.equals(c.getParticipantA()) && !userId.equals(c.getParticipantB())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Conversation introuvable");
        }
        return c;
    }

    private ConversationResponse toDto(ConversationEntity c, boolean withMessages) {
        List<MessageResponse> list = withMessages
                ? messages.findByConversationIdOrderByCreeLeAsc(c.getId()).stream()
                        .map(m -> new MessageResponse(m.getId(), m.getAuteurId(), m.getCorps(), m.getCreeLe()))
                        .toList()
                : List.of();
        return new ConversationResponse(c.getId(), c.getParticipantA(), c.getParticipantB(), c.getUniteId(), list);
    }
}
