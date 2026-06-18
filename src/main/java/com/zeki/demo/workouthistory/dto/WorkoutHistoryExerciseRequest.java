package com.zeki.demo.WorkoutHistory.dto;

import com.zeki.demo.workouts.enums.MuscleType;

public record WorkoutHistoryExerciseRequest(
        String id,
        String name,
        MuscleType muscleType,
        Boolean completed
) {
}
