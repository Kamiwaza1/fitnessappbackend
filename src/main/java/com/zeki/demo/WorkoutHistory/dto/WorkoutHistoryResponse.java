package com.zeki.demo.WorkoutHistory.dto;

import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.enums.MuscleType;
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
        LocalDate updatedAt,
        LocalDate createdAt
) {
}
