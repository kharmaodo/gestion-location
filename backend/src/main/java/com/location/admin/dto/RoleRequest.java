package com.location.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record RoleRequest(@NotBlank String role, @NotBlank String action) {}
