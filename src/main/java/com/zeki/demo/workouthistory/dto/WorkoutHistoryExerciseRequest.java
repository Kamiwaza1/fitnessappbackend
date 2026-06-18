package com.zeki.demo.workouthistory.dto;

import com.zeki.demo.workouts.enums.MuscleType;

public record WorkoutHistoryExerciseRequest(
        String id,
        String name,
        MuscleType muscleType,
        Boolean completed
) {
}
