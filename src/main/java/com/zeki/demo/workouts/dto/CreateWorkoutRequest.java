package com.zeki.demo.workouts.dto;

import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.enums.MuscleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateWorkoutRequest(
        @NotBlank(message = "Workout name is required")
        String workoutName,
        @NotNull(message = "Workout date is required")
        LocalDate workoutDate,
        @NotNull(message = "Duration minutes is required")
        Integer durationMinutes,
        @NotNull(message = "Sets is required")
        Integer sets,
        @NotNull(message = "Reps is required")
        Integer reps,
        @NotNull(message = "Weight (kg) is required")
        Double weightKg,
        String notes,
        String description,
        WorkoutCategory category,
        MuscleType muscleType,
        List<WorkoutExerciseRequest> exercises,
        UUID workoutId
) {
}
