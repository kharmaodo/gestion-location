package com.location.contrats.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SignatureInviteRequest(@NotNull UUID contratId, @NotBlank String roleSignataire, @NotBlank String nomSignataire) {}
