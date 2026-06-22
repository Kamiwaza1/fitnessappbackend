package com.zeki.demo.workouts.exception;

public class WorkoutNotFoundException extends RuntimeException {
    public static final String WORKOUT_NOT_FOUND = "Workout not found";

    public WorkoutNotFoundException() {
        super(WORKOUT_NOT_FOUND);
    }

    public WorkoutNotFoundException(String message) {
        super(message);
    }
}
