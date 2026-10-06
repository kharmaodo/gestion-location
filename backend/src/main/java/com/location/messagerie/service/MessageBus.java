package com.location.messagerie.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class MessageBus {
    private final Map<UUID, List<String>> recent = new ConcurrentHashMap<>();

    public void publish(UUID conversationId, String corps) {
        recent.computeIfAbsent(conversationId, ignored -> new ArrayList<>()).add(corps);
    }

    public List<String> recent(UUID conversationId) {
        return List.copyOf(recent.getOrDefault(conversationId, List.of()));
    }
}
