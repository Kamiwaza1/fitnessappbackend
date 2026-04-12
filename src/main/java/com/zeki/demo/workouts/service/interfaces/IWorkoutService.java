package com.zeki.demo.workouts.service.interfaces;

import com.zeki.demo.workouts.dto.CreateWorkoutRequest;
import com.zeki.demo.workouts.dto.UpdateWorkoutRequest;
import com.zeki.demo.workouts.dto.WorkoutResponse;

import java.util.List;

public interface IWorkoutService {
    WorkoutResponse createWorkout(String userEmail, CreateWorkoutRequest request);
    List<WorkoutResponse> getWorkouts(String userEmail);
    WorkoutResponse getWorkoutById(String userEmail, Long workoutId);
    WorkoutResponse updateWorkout(String userEmail, Long workoutId, UpdateWorkoutRequest request);
    void deleteWorkout(String userEmail, Long workoutId);
}
