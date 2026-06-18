package com.zeki.demo.users.dto;

import jakarta.validation.constraints.NotBlank;

public record AccountDeletionRequest(
        @NotBlank(message = "Password is required")
        String password
) {
}
