package com.zeki.demo.workouts.domain;

import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.enums.WorkoutDifficulty;
import com.zeki.demo.workouts.exception.WorkoutValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Workout {
    private final UUID workoutId;
    private String workoutName;
    private String description;
    private WorkoutCategory category;
    private MuscleType muscleType;
    private WorkoutDifficulty difficulty;
    private final LocalDate createdAt;
    private LocalDate updatedAt;
    private final LocalDate workoutDate;
    private final Integer durationMinutes;
    private final Integer sets;
    private final Integer reps;
    private final Double weightKg;
    private final boolean completed;
    private String notes;
    private final List<WorkoutExercise> exercises;
    private final String userEmail;

    public Workout(
            String workoutName,
            LocalDate workoutDate,
            Integer durationMinutes,
            Integer sets,
            Integer reps,
            Double weightKg,
            String notes,
            UUID workoutId,
            String description,
            WorkoutCategory category,
            MuscleType muscleType,
            List<WorkoutExercise> exercises,
            LocalDate createdAt,
            LocalDate updatedAt,
            String userEmail
    ) {
        this(
                workoutName,
                workoutDate,
                durationMinutes,
                sets,
                reps,
                weightKg,
                notes,
                workoutId,
                description,
                category,
                muscleType,
                WorkoutDifficulty.BEGINNER,
                false,
                exercises,
                createdAt,
                updatedAt,
                userEmail
        );
    }

    public Workout(
            String workoutName,
            LocalDate workoutDate,
            Integer durationMinutes,
            Integer sets,
            Integer reps,
            Double weightKg,
            String notes,
            UUID workoutId,
            String description,
            WorkoutCategory category,
            MuscleType muscleType,
            WorkoutDifficulty difficulty,
            List<WorkoutExercise> exercises,
            LocalDate createdAt,
            LocalDate updatedAt,
            String userEmail
    ) {
        this(
                workoutName,
                workoutDate,
                durationMinutes,
                sets,
                reps,
                weightKg,
                notes,
                workoutId,
                description,
                category,
                muscleType,
                difficulty,
                false,
                exercises,
                createdAt,
                updatedAt,
                userEmail
        );
    }

    public Workout(
            String workoutName,
            LocalDate workoutDate,
            Integer durationMinutes,
            Integer sets,
            Integer reps,
            Double weightKg,
            String notes,
            UUID workoutId,
            String description,
            WorkoutCategory category,
            MuscleType muscleType,
            WorkoutDifficulty difficulty,
            boolean completed,
            List<WorkoutExercise> exercises,
            LocalDate createdAt,
            LocalDate updatedAt,
            String userEmail
    ) {
        this.workoutId = workoutId;
        this.workoutName = normalize(workoutName);
        this.workoutDate = workoutDate;
        this.durationMinutes = durationMinutes;
        this.sets = sets;
        this.reps = reps;
        this.weightKg = weightKg;
        this.completed = completed;
        this.notes = normalizeNullable(notes);
        this.description = normalizeNullable(description);
        this.category = category;
        this.muscleType = muscleType;
        this.difficulty = difficulty == null ? WorkoutDifficulty.BEGINNER : difficulty;
        this.exercises = exercises == null ? List.of() : new ArrayList<>(exercises);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userEmail = normalize(userEmail);
    }

    public Workout markCompleted() {
        return new Workout(
                workoutName,
                workoutDate,
                durationMinutes,
                sets,
                reps,
                weightKg,
                notes,
                workoutId,
                description,
                category,
                muscleType,
                difficulty,
                true,
                exercises,
                createdAt,
                LocalDate.now(),
                userEmail
        );
    }

    public void validate() {

        if (workoutName.length() < 3) {
            throw new WorkoutValidationException("Name must be at least 3 characters");
        }
        if (workoutName.length() > 255) {
            throw new WorkoutValidationException("Name cannot be longer than 255 characters");
        }
        if (workoutDate == null) {
            throw new WorkoutValidationException("Workout date is required");
        }
        if (workoutDate.isBefore(LocalDate.now())) {
            throw new WorkoutValidationException("Workout date cannot be in the past");
        }
        if (workoutDate.isAfter(LocalDate.now().plusYears(1))) {
            throw new WorkoutValidationException("Workout date cannot be more than one year ahead");
        }
        if (durationMinutes != null && durationMinutes < 1) {
            throw new WorkoutValidationException("Duration must be at least 1 minute");
        }
        if (durationMinutes != null && durationMinutes > 999) {
            throw new WorkoutValidationException("Duration cannot be greater than 999 minutes");
        }
        if (sets == null || sets < 1) {
            throw new WorkoutValidationException("Sets must be at least 1");
        }
        if (reps == null || reps < 1) {
            throw new WorkoutValidationException("Reps must be at least 1");
        }
        if (weightKg == null || weightKg < 0) {
            throw new WorkoutValidationException("Weight (kg) must be 0 or greater");
        }
        if (description != null && description.length() > 255) {
            throw new WorkoutValidationException("Description cannot be longer than 255 characters");
        }
        if (category == null) {
            throw new WorkoutValidationException("Workout category is required");
        }
        if (muscleType == null) {
            throw new WorkoutValidationException("Muscle type is required");
        }
        if (difficulty == null) {
            throw new WorkoutValidationException("Workout difficulty is required");
        }
        if (!exercises.isEmpty()) {
            for (WorkoutExercise exercise : exercises) {
                exercise.validate();
            }
        }
        if (notes != null && notes.length() > 1000) {
            throw new WorkoutValidationException("Notes cannot be longer than 1000 characters");
        }
        if (createdAt == null) {
            throw new WorkoutValidationException("Created at is required");
        }
        if (createdAt.isAfter(LocalDate.now())) {
            throw new WorkoutValidationException("Created at cannot be in the future");
        }
        if (updatedAt == null) {
            throw new WorkoutValidationException("Updated at is required");
        }
        if (updatedAt.isAfter(LocalDate.now())) {
            throw new WorkoutValidationException("Updated at cannot be in the future");
        }
        if (updatedAt.isBefore(createdAt)) {
            throw new WorkoutValidationException("Updated at cannot be before created at");
        }
        if (userEmail == null || userEmail.isBlank()) {
            throw new WorkoutValidationException("User email cannot be blank");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeNullable(String value) {
        return value == null ? null : value.trim();
    }
}
