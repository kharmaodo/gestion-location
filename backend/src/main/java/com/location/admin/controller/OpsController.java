package com.location.admin.controller;

import com.location.admin.dto.OpsSnapshotResponse;
import com.location.contrats.repository.SignatureRepository;
import com.location.litiges.repository.LitigeRepository;
import com.location.paiements.repository.IntentionPaiementRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/ops")
@PreAuthorize("hasRole('ADMIN')")
public class OpsController {
    private final IntentionPaiementRepository intentions;
    private final SignatureRepository signatures;
    private final LitigeRepository litiges;

    public OpsController(
            IntentionPaiementRepository intentions, SignatureRepository signatures, LitigeRepository litiges) {
        this.intentions = intentions;
        this.signatures = signatures;
        this.litiges = litiges;
    }

    @GetMapping
    public OpsSnapshotResponse snapshot() {
        return new OpsSnapshotResponse(
                intentions.countByStatut("EN_ATTENTE"),
                intentions.countByStatut("ECHEC"),
                intentions.countByStatut("REUSSI"),
                signatures.countByStatut("EN_ATTENTE"),
                signatures.countByStatut("SIGNE"),
                litiges.countByStatut("OUVERT"));
    }
}
