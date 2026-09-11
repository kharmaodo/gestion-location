package com.location.identites.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String typeCompte,
        @Email String email,
        String telephone,
        @NotBlank @Size(min = 8, max = 72) String motDePasse,
        String prenom,
        String nom,
        @AssertTrue boolean consentementRgpd) {}
