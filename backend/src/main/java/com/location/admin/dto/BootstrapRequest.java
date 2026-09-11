package com.location.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record BootstrapRequest(@NotBlank String token) {}
