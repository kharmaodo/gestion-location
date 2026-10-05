package com.location.contrats.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.location.contrats.dto.CautionSimulationRequest;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class DocumentsEtCautionTest {
    @Test
    void pdfCommenceParLEntete() {
        byte[] pdf = SimplePdf.fromLines(List.of("Contrat (test)"));
        String head = new String(pdf, 0, 8, StandardCharsets.ISO_8859_1);
        assertTrue(head.startsWith("%PDF-1.4"));
    }

    @Test
    void journalierVautTrenteJoursPlafonne() {
        var res = new CautionService().simuler(new CautionSimulationRequest(new BigDecimal("1000"), "JOURNALIER", 4));
        org.junit.jupiter.api.Assertions.assertEquals(3, res.moisRetenus());
        org.junit.jupiter.api.Assertions.assertEquals(new BigDecimal("90000"), res.cautionCalculee());
    }
}
