package com.zeki.demo.liveworkouts.domain;

import java.time.Instant;

public record LiveWorkoutParticipant(
        String email,
        String displayName,
        String status,
        Integer currentExerciseIndex,
        Integer completedSets,
        Instant joinedAt
) {
    public static LiveWorkoutParticipant joined(String email) {
        return new LiveWorkoutParticipant(
                email,
                toDisplayName(email),
                "ACTIVE",
                0,
                0,
                Instant.now()
        );
    }

    public LiveWorkoutParticipant withProgress(Integer currentExerciseIndex, Integer completedSets, String status) {
        return new LiveWorkoutParticipant(
                email,
                displayName,
                status == null || status.isBlank() ? this.status : status.trim(),
                Math.max(0, currentExerciseIndex == null ? this.currentExerciseIndex : currentExerciseIndex),
                Math.max(0, completedSets == null ? this.completedSets : completedSets),
                joinedAt
        );
    }

    private static String toDisplayName(String email) {
        if (email == null || email.isBlank()) {
            return "User";
        }
        int atIndex = email.indexOf("@");
        return atIndex > 0 ? email.substring(0, atIndex) : email;
    }
}
