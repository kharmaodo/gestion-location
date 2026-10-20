package com.location.messagerie.service;

import com.location.identites.entity.UtilisateurEntity;
import com.location.identites.repository.UtilisateurRepository;
import com.location.identites.repository.UtilisateurRoleRepository;
import com.location.messagerie.dto.ConversationRequest;
import com.location.messagerie.dto.ConversationResponse;
import com.location.messagerie.dto.DestinataireResponse;
import com.location.messagerie.dto.MessageResponse;
import com.location.messagerie.entity.ConversationEntity;
import com.location.messagerie.entity.MessageEntity;
import com.location.messagerie.repository.ConversationRepository;
import com.location.messagerie.repository.MessageRepository;
import com.location.shared.exception.ApiException;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
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
    private final UtilisateurRoleRepository roles;
    private final MessageBus bus;

    public MessagerieService(
            ConversationRepository conversations,
            MessageRepository messages,
            UtilisateurRepository utilisateurs,
            UtilisateurRoleRepository roles,
            MessageBus bus) {
        this.conversations = conversations;
        this.messages = messages;
        this.utilisateurs = utilisateurs;
        this.roles = roles;
        this.bus = bus;
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> lister(UUID userId) {
        return conversations.findMine(userId).stream().map(c -> toDto(c, false)).toList();
    }

    @Transactional(readOnly = true)
    public ConversationResponse detail(UUID userId, UUID id) {
        return toDto(owned(userId, id), true);
    }

    @Transactional(readOnly = true)
    public List<DestinataireResponse> destinataires(UUID userId) {
        String cible = roles.findByUtilisateurId(userId).stream()
                .anyMatch(r -> "PROPRIETAIRE".equals(r.getRole())) ? "LOCATAIRE" : "PROPRIETAIRE";
        LinkedHashMap<String, DestinataireResponse> uniques = new LinkedHashMap<>();
        utilisateurs.findAll().stream()
                .filter(u -> !userId.equals(u.getId()))
                .filter(u -> "ACTIF".equals(u.getStatut()))
                .filter(u -> roles.findByUtilisateurId(u.getId()).stream().anyMatch(r -> cible.equals(r.getRole())))
                .filter(u -> !vide(u.getPrenom()) || !vide(u.getNom()))
                .sorted(Comparator.comparing(UtilisateurEntity::getId))
                .forEach(u -> uniques.putIfAbsent(cle(u), toDestinataire(u)));
        return List.copyOf(uniques.values());
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
        bus.publish(c.getId(), m.getCorps());
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

    private DestinataireResponse toDestinataire(UtilisateurEntity u) {
        return new DestinataireResponse(u.getId(), vide(u.getPrenom()) ? "" : u.getPrenom(), vide(u.getNom()) ? "" : u.getNom(), u.getEmail());
    }

    private static boolean vide(String s) { return s == null || s.isBlank(); }

    private static String cle(UtilisateurEntity u) {
        return u.getEmail() == null ? u.getId().toString() : u.getEmail().toLowerCase();
    }
}
