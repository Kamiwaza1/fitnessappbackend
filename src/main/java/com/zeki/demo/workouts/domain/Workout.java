package com.zeki.demo.workouts.domain;

import com.zeki.demo.workouts.enums.WorkoutCategory;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Workout {
    private final UUID workoutId;
    private String workoutName;
    private String description;
    private WorkoutCategory category;
    private final LocalDate createdAt;
    private LocalDate updatedAt;
    private LocalDate workoutDate;
    private Integer durationMinutes;
    private String notes;
    private String userEmail;

    public Workout(
            String workoutName,
            LocalDate workoutDate,
            int durationMinutes,
            String notes,
            UUID workoutId,
            String description,
            WorkoutCategory category,
            LocalDate createdAt,
            LocalDate updatedAt,
            String userEmail
    ) {
        this.workoutId = workoutId;
        this.workoutName = normalize(workoutName);
        this.workoutDate = workoutDate;
        this.durationMinutes = durationMinutes;
        this.notes = normalizeNullable(notes);
        this.description = normalizeNullable(description);
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userEmail = userEmail;
    }

    public void patch(
            String workoutName,
            LocalDate workoutDate,
            Integer durationMinutes,
            String notes,
            WorkoutCategory category
    ) {
        if (workoutName != null) {
            this.workoutName = normalize(workoutName);
        }

        if (workoutDate != null) {
            this.workoutDate = workoutDate;
        }

        if (durationMinutes != null) {
            this.durationMinutes = durationMinutes;
        }

        if (notes != null) {
            this.notes = normalizeNullable(notes);
        }

        if (category != null) {
            this.category = category;
        }
    }

    public void markUpdated() {
        this.updatedAt = LocalDate.now();
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeNullable(String value) {
        return value == null ? null : value.trim();
    }
}
