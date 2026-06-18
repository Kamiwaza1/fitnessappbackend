package com.zeki.demo.workouts.WorkoutException;


public class WorkoutExistException extends RuntimeException {
    public WorkoutExistException(String message) {
        super(message);
    }
}
