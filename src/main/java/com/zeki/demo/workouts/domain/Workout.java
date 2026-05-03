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
    private final LocalDate workoutDate;
    private final Integer durationMinutes;
    private String notes;
    private final String userEmail;

    public Workout(
            String workoutName,
            LocalDate workoutDate,
            Integer durationMinutes,
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
        this.userEmail = normalize(userEmail);
    }

    public void validate() {
        if (workoutName == null || workoutName.isBlank()) {
            throw new IllegalArgumentException("Workout name is required");
        }
        if (workoutName.length() < 3) {
            throw new IllegalArgumentException("Name must be at least 3 characters");
        }
        if (workoutName.length() > 255) {
            throw new IllegalArgumentException("Name cannot be longer than 255 characters");
        }
        if (workoutDate == null) {
            throw new IllegalArgumentException("Workout date is required");
        }
        if (workoutDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Workout date cannot be in the past");
        }
        if (workoutDate.isAfter(LocalDate.now().plusYears(1))) {
            throw new IllegalArgumentException("Workout date cannot be more than one year ahead");
        }
        if (durationMinutes == null) {
            throw new IllegalArgumentException("Duration is required");
        }
        if (durationMinutes < 1) {
            throw new IllegalArgumentException("Duration must be at least 1 minute");
        }
        if (durationMinutes > 999) {
            throw new IllegalArgumentException("Duration cannot be greater than 999 minutes");
        }
        if (description != null && description.length() > 255) {
            throw new IllegalArgumentException("Description cannot be longer than 255 characters");
        }
        if (category == null) {
            throw new IllegalArgumentException("Workout category is required");
        }
        if (notes != null && notes.length() > 1000) {
            throw new IllegalArgumentException("Notes cannot be longer than 1000 characters");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Created at is required");
        }
        if (createdAt.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Created at cannot be in the future");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("Updated at is required");
        }
        if (updatedAt.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Updated at cannot be in the future");
        }
        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("Updated at cannot be before created at");
        }
        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException("User email cannot be blank");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeNullable(String value) {
        return value == null ? null : value.trim();
    }
}
