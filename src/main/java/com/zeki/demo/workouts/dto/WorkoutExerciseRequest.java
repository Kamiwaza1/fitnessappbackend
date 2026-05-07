package com.zeki.demo.workouts.dto;

import com.zeki.demo.workouts.enums.MuscleType;

public record WorkoutExerciseRequest(
        String name,
        MuscleType muscleType,
        Integer sets,
        Integer reps,
        Double weightKg,
        String notes
) {
}
