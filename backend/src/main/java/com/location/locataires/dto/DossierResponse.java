package com.location.locataires.dto;

import java.util.List;
import java.util.UUID;

public record DossierResponse(
        UUID id,
        String prenom,
        String nom,
        String telephone,
        String email,
        String pieceType,
        String pieceNumero,
        String kycStatut,
        String kycCommentaire,
        List<DocumentResponse> documents) {}
