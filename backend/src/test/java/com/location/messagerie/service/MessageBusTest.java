package com.location.messagerie.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class MessageBusTest {
    @Test
    void unMessagePublieEstVisibleDansLeFlux() {
        MessageBus bus = new MessageBus();
        UUID id = UUID.randomUUID();
        bus.publish(id, "bonjour");
        assertEquals(List.of("bonjour"), bus.recent(id));
        assertEquals(0, bus.recent(UUID.randomUUID()).size());
    }
}
