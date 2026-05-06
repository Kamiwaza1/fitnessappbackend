package com.zeki.demo.workouts.dto;

import com.zeki.demo.workouts.enums.WorkoutCategory;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateWorkoutRequest(
        UUID workoutId,
        String workoutName,
        LocalDate workoutDate,
        Integer durationMinutes,
        Integer sets,
        Integer reps,
        Double weightKg,
        String notes,
        WorkoutCategory category,
        String description
) {
}
