package com.zeki.demo.workouts.dto;

import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.enums.MuscleType;
import java.time.LocalDate;
import java.util.List;
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
        MuscleType muscleType,
        List<WorkoutExerciseRequest> exercises,
        String description
) {
}
