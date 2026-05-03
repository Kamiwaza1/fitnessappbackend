package com.zeki.demo.workouts.dto;

import com.zeki.demo.workouts.enums.WorkoutCategory;
import java.time.LocalDate;
import java.util.UUID;

public record WorkoutResponse(
        UUID workoutId,
        LocalDate workoutDate,
        Integer durationMinutes,
        String workoutName,
        String notes,
        String description,
        WorkoutCategory category,
        LocalDate updatedAt,
        LocalDate createdAt
) {
}
