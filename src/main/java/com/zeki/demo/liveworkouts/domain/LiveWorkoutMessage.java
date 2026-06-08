package com.zeki.demo.liveworkouts.domain;

import java.time.Instant;

public record LiveWorkoutMessage(
        String messageId,
        String roomCode,
        String senderEmail,
        String senderName,
        String text,
        Instant createdAt
) {
    public static LiveWorkoutMessage create(String roomCode, String senderEmail, String text) {
        Instant now = Instant.now();
        return new LiveWorkoutMessage(
                null,
                normalizeRoomCode(roomCode),
                senderEmail,
                toDisplayName(senderEmail),
                normalizeText(text),
                now
        );
    }

    private static String normalizeRoomCode(String roomCode) {
        return roomCode == null ? null : roomCode.trim().toUpperCase();
    }

    private static String normalizeText(String text) {
        return text == null ? "" : text.trim();
    }

    private static String toDisplayName(String email) {
        if (email == null || email.isBlank()) {
            return "User";
        }
        int atIndex = email.indexOf("@");
        return atIndex > 0 ? email.substring(0, atIndex) : email;
    }
}
