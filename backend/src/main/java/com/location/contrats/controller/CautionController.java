package com.location.contrats.controller;

import com.location.contrats.dto.CautionSimulationRequest;
import com.location.contrats.dto.CautionSimulationResponse;
import com.location.contrats.service.CautionService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CautionController {
    private final CautionService service;

    public CautionController(CautionService service) {
        this.service = service;
    }

    @GetMapping({"/api/v1/public/caution", "/api/v1/caution"})
    public CautionSimulationResponse get(
            @RequestParam BigDecimal loyer,
            @RequestParam(required = false, defaultValue = "MENSUEL") String periodicite,
            @RequestParam(required = false, defaultValue = "1") Integer mois) {
        return service.simuler(new CautionSimulationRequest(loyer, periodicite, mois));
    }

    @PostMapping({"/api/v1/public/caution", "/api/v1/caution"})
    public CautionSimulationResponse post(@Valid @RequestBody CautionSimulationRequest request) {
        return service.simuler(request);
    }
}
