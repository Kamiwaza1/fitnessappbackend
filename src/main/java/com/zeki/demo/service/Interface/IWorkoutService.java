package com.zeki.demo.service.Interface;

import com.zeki.demo.dto.CreateWorkoutRequest;
import com.zeki.demo.dto.UpdateWorkoutRequest;
import com.zeki.demo.dto.WorkoutResponse;

import java.util.List;

public interface IWorkoutService {
    WorkoutResponse createWorkout(String userEmail, CreateWorkoutRequest request);
    List<WorkoutResponse> getWorkouts(String userEmail);
    WorkoutResponse getWorkoutById(String userEmail, Long workoutId);
    WorkoutResponse updateWorkout(String userEmail, Long workoutId, UpdateWorkoutRequest request);
    void deleteWorkout(String userEmail, Long workoutId);
}
