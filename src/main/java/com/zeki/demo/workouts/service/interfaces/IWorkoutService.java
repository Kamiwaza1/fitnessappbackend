package com.zeki.demo.workouts.service.interfaces;

import com.zeki.demo.workouts.dto.CreateWorkoutRequest;
import com.zeki.demo.workouts.dto.UpdateWorkoutRequest;
import com.zeki.demo.workouts.dto.WorkoutResponse;

import java.util.List;
import java.util.UUID;

public interface IWorkoutService {
    WorkoutResponse createWorkout(String userEmail, CreateWorkoutRequest request);
    List<WorkoutResponse> getWorkouts(String userEmail);
    WorkoutResponse getWorkoutById(String userEmail, UUID workoutId);
    WorkoutResponse updateWorkout(String userEmail, UUID workoutId, UpdateWorkoutRequest request);
    void deleteWorkout(String userEmail, UUID workoutId);
    String getDashboardData(String userEmail);
}
