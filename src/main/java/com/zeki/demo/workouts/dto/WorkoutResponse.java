package com.zeki.demo.workouts.dto;

import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.enums.WorkoutDifficulty;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record WorkoutResponse(
        UUID workoutId,
        LocalDate workoutDate,
        Integer durationMinutes,
        Integer sets,
        Integer reps,
        Double weightKg,
        boolean completed,
        String workoutName,
        String notes,
        String description,
        WorkoutCategory category,
        MuscleType muscleType,
        WorkoutDifficulty difficulty,
        List<WorkoutExerciseResponse> exercises,
        LocalDate updatedAt,
        LocalDate createdAt
) {
    public WorkoutResponse(
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
            List<WorkoutExerciseResponse> exercises,
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
                false,
                workoutName,
                notes,
                description,
                category,
                muscleType,
                null,
                exercises,
                updatedAt,
                createdAt
        );
    }
}
