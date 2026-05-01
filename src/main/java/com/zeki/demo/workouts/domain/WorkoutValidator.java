package com.zeki.demo.workouts.domain;

import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class WorkoutValidator {

    public void validateWorkout(Workout workout) {
        if (workout == null) {
            throw new IllegalArgumentException("Workout payload is required");
        }
        if (workout.getWorkoutName()== null || workout.getWorkoutName().isBlank()) {
            throw new IllegalArgumentException("Workout name is required");
        }
        if (workout.getWorkoutName().length() < 3) {
            throw new IllegalArgumentException("Name must be at least 3 characters");
        }
        if (workout.getWorkoutName().length() > 255) {
            throw new IllegalArgumentException("Name cannot be longer than 255 characters");
        }

        if (workout.getWorkoutDate() == null) {
            throw new IllegalArgumentException("Workout date is required");
        }
        if (workout.getWorkoutDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Workout date cannot be in the past");
        }
        if (workout.getWorkoutDate().isAfter(LocalDate.now().plusYears(1))) {
            throw new IllegalArgumentException("Workout date cannot be more than one year ahead");
        }
        if (workout.getDurationMinutes() == null) {
            throw new IllegalArgumentException("Duration is required");
        }
        if (workout.getDurationMinutes() < 1) {
            throw new IllegalArgumentException("Duration must be at least 1 minute");
        }
        if (workout.getDurationMinutes() > 999) {
            throw new IllegalArgumentException("Duration cannot be greater than 999 minutes");
        }
        if (workout.getDescription() != null && workout.getDescription().length() > 255) {
            throw new IllegalArgumentException("Description cannot be longer than 255 characters");
        }
        if (workout.getCategory() == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        if (workout.getNotes() != null && workout.getNotes().length() > 1000) {
            throw new IllegalArgumentException("Notes cannot be longer than 1000 characters");
        }
        if (workout.getCreatedAt() == null) {
            throw new IllegalArgumentException("Created at is required");
        }
        if (workout.getCreatedAt().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Created at cannot be in the future");
        }
        if (workout.getUpdatedAt() == null) {
            throw new IllegalArgumentException("Updated at is required");
        }
        if (workout.getUpdatedAt().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Updated at cannot be in the future");
        }
        if (workout.getUpdatedAt().isBefore(workout.getCreatedAt())) {
            throw new IllegalArgumentException("Updated at cannot be before created at");
        }
        if (workout.getUserEmail() == null || workout.getUserEmail().isBlank()) {
            throw new IllegalArgumentException("User email cannot be blank");
        }
    }
}
