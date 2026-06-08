package com.zeki.demo.liveworkouts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LiveWorkoutMessageRequest(
        @NotBlank(message = "Message text is required")
        @Size(max = 500, message = "Message cannot be longer than 500 characters")
        String text
) {
}
