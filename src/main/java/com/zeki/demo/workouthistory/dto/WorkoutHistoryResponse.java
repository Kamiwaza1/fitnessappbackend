package com.zeki.demo.WorkoutHistory.dto;

import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.enums.WorkoutDifficulty;
import java.time.LocalDate;
import java.util.UUID;

public record WorkoutHistoryResponse(
        UUID workoutId,
        LocalDate workoutDate,
        Integer durationMinutes,
        Integer sets,
        Integer reps,
        Double weightKg,
        String workoutName,
        String notes,
        String description,
        WorkoutCategory category,
        MuscleType muscleType,
        WorkoutDifficulty difficulty,
        LocalDate updatedAt,
        LocalDate createdAt
) {
    public WorkoutHistoryResponse(
            UUID workoutId,
            LocalDate workoutDate,
            Integer durationMinutes,
            Integer sets,
            Integer reps,
            Double weightKg,
            String workoutName,
            String notes,
            String description,
            WorkoutCategory category,
            MuscleType muscleType,
            LocalDate updatedAt,
            LocalDate createdAt
    ) {
        this(
                workoutId,
                workoutDate,
                durationMinutes,
                sets,
                reps,
                weightKg,
                workoutName,
                notes,
                description,
                category,
                muscleType,
                null,
                updatedAt,
                createdAt
        );
    }
}
