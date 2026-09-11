package com.location.contrats.dto;

import java.time.Instant;
import java.util.UUID;

public record SignatureResponse(
        UUID id, UUID contratId, String roleSignataire, String nomSignataire, String statut, Instant signeLe, String lien) {}
