package com.location.contrats;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.location.contrats.dto.CautionSimulationRequest;
import com.location.contrats.service.CautionService;
import com.location.shared.exception.ApiException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class CautionServiceTest {
    private final CautionService service = new CautionService();

    @Test
    void plafonneATroisMois() {
        var res = service.simuler(new CautionSimulationRequest(new BigDecimal("85000"), "MENSUEL", 5));
        assertEquals(3, res.moisRetenus());
        assertEquals(new BigDecimal("255000"), res.cautionCalculee());
    }

    @Test
    void refuseUneCautionAuDessusDuPlafond() {
        ApiException ex = assertThrows(ApiException.class, () ->
                service.assertCautionSousPlafond(new BigDecimal("400000"), new BigDecimal("85000"), "MENSUEL"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }
}
