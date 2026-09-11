package com.location.biens.dto;

import jakarta.validation.constraints.NotBlank;

public record MediaRequest(@NotBlank String url, String type) {}
