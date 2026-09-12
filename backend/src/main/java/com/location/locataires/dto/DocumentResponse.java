package com.location.locataires.dto;

import java.time.Instant;
import java.util.UUID;

public record DocumentResponse(
        UUID id, String type, String nomFichier, String chemin, String mime, String kycStatut, Instant creeLe) {}
