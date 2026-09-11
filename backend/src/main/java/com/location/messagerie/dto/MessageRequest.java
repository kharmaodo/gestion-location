package com.location.messagerie.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageRequest(@NotBlank String corps) {}
