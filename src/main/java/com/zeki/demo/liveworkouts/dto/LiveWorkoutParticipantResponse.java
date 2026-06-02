package com.zeki.demo.liveworkouts.dto;

import java.time.Instant;

public record LiveWorkoutParticipantResponse(
        String email,
        String displayName,
        String status,
        Integer currentExerciseIndex,
        Integer completedSets,
        Instant joinedAt
) {
}
