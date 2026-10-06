package com.location.messagerie.controller;

import com.location.messagerie.dto.ConversationRequest;
import com.location.messagerie.dto.ConversationResponse;
import com.location.messagerie.dto.MessageRequest;
import com.location.messagerie.dto.MessageResponse;
import com.location.messagerie.service.MessageBus;
import com.location.messagerie.service.MessagerieService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/conversations")
public class MessagerieController {
    private final MessagerieService service;
    private final MessageBus bus;

    public MessagerieController(MessagerieService service, MessageBus bus) {
        this.service = service;
        this.bus = bus;
    }

    @GetMapping
    public List<ConversationResponse> lister(Authentication auth) {
        return service.lister(UUID.fromString(auth.getName()));
    }

    @GetMapping("/{id}")
    public ConversationResponse detail(Authentication auth, @PathVariable UUID id) {
        return service.detail(UUID.fromString(auth.getName()), id);
    }

    @GetMapping("/{id}/flux")
    public List<String> flux(Authentication auth, @PathVariable UUID id) {
        service.detail(UUID.fromString(auth.getName()), id);
        return bus.recent(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationResponse ouvrir(Authentication auth, @Valid @RequestBody ConversationRequest request) {
        return service.ouvrir(UUID.fromString(auth.getName()), request);
    }

    @PostMapping("/{id}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse ecrire(
            Authentication auth, @PathVariable UUID id, @Valid @RequestBody MessageRequest request) {
        return service.ecrire(UUID.fromString(auth.getName()), id, request.corps());
    }
}
