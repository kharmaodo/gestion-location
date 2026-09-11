package com.location.locataires.dto;

import jakarta.validation.constraints.NotBlank;

public record DossierRequest(
        @NotBlank String nom,
        String prenom,
        String telephone,
        String email,
        String pieceType,
        String pieceNumero) {}
