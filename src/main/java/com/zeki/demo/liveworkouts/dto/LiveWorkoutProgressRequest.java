package com.zeki.demo.liveworkouts.dto;

public record LiveWorkoutProgressRequest(
        Integer currentExerciseIndex,
        Integer completedSets,
        String status
) {
}
