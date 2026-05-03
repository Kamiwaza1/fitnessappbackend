package com.zeki.demo.workouts.dto;

import com.zeki.demo.workouts.enums.WorkoutCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record CreateWorkoutRequest(
        @NotBlank(message = "Workout name is required")
        String workoutName,
        @NotNull(message = "Workout date is required")
        LocalDate workoutDate,
        @NotNull(message = "Duration minutes is required")
        Integer durationMinutes,
        String notes,
        String description,
        WorkoutCategory category,
        UUID workoutId
) {
}
