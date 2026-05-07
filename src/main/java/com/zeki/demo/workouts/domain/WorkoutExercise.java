package com.zeki.demo.workouts.domain;

import com.zeki.demo.workouts.enums.MuscleType;
import lombok.Getter;

@Getter
public class WorkoutExercise {
    private final String name;
    private final MuscleType muscleType;
    private final Integer sets;
    private final Integer reps;
    private final Double weightKg;
    private final String notes;

    public WorkoutExercise(
            String name,
            MuscleType muscleType,
            Integer sets,
            Integer reps,
            Double weightKg,
            String notes
    ) {
        this.name = normalize(name);
        this.muscleType = muscleType;
        this.sets = sets;
        this.reps = reps;
        this.weightKg = weightKg;
        this.notes = normalizeNullable(notes);
    }

    public void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Exercise name is required");
        }
        if (name.length() > 255) {
            throw new IllegalArgumentException("Exercise name cannot be longer than 255 characters");
        }
        if (muscleType == null) {
            throw new IllegalArgumentException("Exercise muscle type is required");
        }
        if (sets == null || sets < 1) {
            throw new IllegalArgumentException("Exercise sets must be at least 1");
        }
        if (reps == null || reps < 1) {
            throw new IllegalArgumentException("Exercise reps must be at least 1");
        }
        if (weightKg == null || weightKg < 0) {
            throw new IllegalArgumentException("Exercise weight (kg) must be 0 or greater");
        }
        if (notes != null && notes.length() > 1000) {
            throw new IllegalArgumentException("Exercise notes cannot be longer than 1000 characters");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeNullable(String value) {
        return value == null ? null : value.trim();
    }
}
