package com.location.litiges.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record LitigeRequest(@NotNull UUID contratId, @NotBlank String motif, @NotBlank String description) {}
