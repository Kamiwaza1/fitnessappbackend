package com.zeki.demo.workouts.dto;

import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.enums.WorkoutDifficulty;
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
        WorkoutDifficulty difficulty,
        List<WorkoutExerciseRequest> exercises,
        String description
) {
    public UpdateWorkoutRequest(
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
        this(
                workoutId,
                workoutName,
                workoutDate,
                durationMinutes,
                sets,
                reps,
                weightKg,
                notes,
                category,
                muscleType,
                null,
                exercises,
                description
        );
    }
}
