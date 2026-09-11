package com.location.identites.dto;

import jakarta.validation.constraints.NotBlank;

public record TwoFactorCodeRequest(@NotBlank String code) {}
