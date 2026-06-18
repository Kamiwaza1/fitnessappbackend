package com.zeki.demo.workouts.workoutexception;


public class WorkoutExistException extends RuntimeException {
    public WorkoutExistException(String message) {
        super(message);
    }
}
