package com.zeki.demo.liveworkouts.dto;

import java.time.Instant;

public record LiveWorkoutMessageResponse(
        String messageId,
        String roomCode,
        String senderEmail,
        String senderName,
        String text,
        Instant createdAt
) {
}
