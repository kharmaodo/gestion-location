package com.location.locataires.dto;

import jakarta.validation.constraints.NotBlank;

public record DocumentMetaRequest(@NotBlank String type, @NotBlank String nomFichier, @NotBlank String chemin, String mime) {}
