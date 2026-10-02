package com.location.canaux;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.location.shared.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class CanalMockServiceTest {
    @Test
    void mockJournaliseSms() {
        CanalMockService service = new CanalMockService("MOCK", "MOCK", "MOCK");
        var row = service.sms("770000000", "loyer");
        assertEquals("MOCK", row.get("mode"));
        assertEquals("sms", row.get("canal"));
        assertEquals(1, ((java.util.List<?>) service.statut().get("journal")).size());
    }

    @Test
    void offRefuse() {
        CanalMockService service = new CanalMockService("OFF", "MOCK", "MOCK");
        ApiException ex = assertThrows(ApiException.class, () -> service.sms("77", "x"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void liveNonBranche() {
        CanalMockService service = new CanalMockService("MOCK", "LIVE", "MOCK");
        ApiException ex = assertThrows(ApiException.class, () -> service.push("token", "x"));
        assertEquals(HttpStatus.NOT_IMPLEMENTED, ex.getStatus());
    }
}
