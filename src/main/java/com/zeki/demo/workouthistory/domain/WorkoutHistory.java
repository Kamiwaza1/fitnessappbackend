package com.zeki.demo.workouthistory.domain;

import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.enums.WorkoutDifficulty;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;

@Getter
public class WorkoutHistory {
    private final UUID workoutId;
    private final LocalDate workoutDate;
    private final Integer durationMinutes;
    private final Integer sets;
    private final Integer reps;
    private final Double weightKg;
    private final String workoutName;
    private final String notes;
    private final String description;
    private final WorkoutCategory category;
    private final MuscleType muscleType;
    private final WorkoutDifficulty difficulty;
    private final LocalDate updatedAt;
    private final LocalDate createdAt;
    private final String userEmail;

    public WorkoutHistory(
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
            LocalDate createdAt,
            String userEmail
    ) {
        this(
                workoutId,
                workoutDate,
                durationMinutes,
                sets,
                reps,
                weightKg,
                workoutName,
                notes,
                description,
                category,
                muscleType,
                WorkoutDifficulty.BEGINNER,
                updatedAt,
                createdAt,
                userEmail
        );
    }

    public WorkoutHistory(
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
            WorkoutDifficulty difficulty,
            LocalDate updatedAt,
            LocalDate createdAt,
            String userEmail
    ) {
        this.workoutId = workoutId;
        this.workoutDate = workoutDate;
        this.durationMinutes = durationMinutes;
        this.sets = sets;
        this.reps = reps;
        this.weightKg = weightKg;
        this.workoutName = workoutName;
        this.notes = notes;
        this.description = description;
        this.category = category;
        this.muscleType = muscleType;
        this.difficulty = difficulty == null ? WorkoutDifficulty.BEGINNER : difficulty;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.userEmail = userEmail;
    }
}
