package com.zeki.demo.workouthistory.dto;

import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.enums.WorkoutDifficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateWorkoutHistoryRequest(
        @NotNull(message = "Workout ID is required")
        UUID workoutId,
        @NotBlank(message = "Workout name is required")
        String workoutName,
        LocalDate workoutDate,
        Instant startedAt,
        Instant finishedAt,
        Integer durationMinutes,
        Integer sets,
        Integer reps,
        Double weightKg,
        String notes,
        String description,
        WorkoutCategory category,
        MuscleType muscleType,
        WorkoutDifficulty difficulty,
        List<WorkoutHistoryExerciseRequest> exercises
) {
}
