package com.zeki.demo.workouts.dto;

import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.enums.WorkoutDifficulty;
import java.time.LocalDate;
import java.util.List;

public record UpdateWorkoutRequest(
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
