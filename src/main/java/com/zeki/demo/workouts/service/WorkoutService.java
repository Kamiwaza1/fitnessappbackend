package com.zeki.demo.workouts.service;

import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.dto.UpdateWorkoutRequest;
import java.util.List;
import java.util.UUID;

public interface WorkoutService {

    Workout createWorkout(Workout workout);

    Workout getWorkoutById(UUID workoutId, String userEmail);

    List<Workout> getMyWorkouts(String userEmail);
    List<Workout> upcomingWorkouts(String userEmail);

    Workout updateWorkout(UUID workoutId, UpdateWorkoutRequest request, String userEmail);

    void deleteWorkout(UUID workoutId, String userEmail);
}
