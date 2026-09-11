package com.location.paiements.controller;

import com.location.paiements.dto.IntentionResponse;
import com.location.paiements.dto.WebhookRequest;
import com.location.paiements.service.PaiementEnLigneService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/paiements")
public class PaiementWebhookController {
    private final PaiementEnLigneService enLigne;

    public PaiementWebhookController(PaiementEnLigneService enLigne) {
        this.enLigne = enLigne;
    }

    @PostMapping("/webhook")
    public IntentionResponse webhook(@Valid @RequestBody WebhookRequest request) {
        return enLigne.webhook(request.intentionId(), request.statut());
    }
}
