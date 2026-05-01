package com.zeki.demo.workouts.service;

import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.dto.CreateWorkoutRequest;
import com.zeki.demo.workouts.dto.DeleteWorkoutRequest;
import com.zeki.demo.workouts.dto.UpdateWorkoutRequest;

import java.util.List;
import java.util.UUID;

public interface WorkoutService {

    Workout createWorkout(Workout workout);

    Workout getWorkoutById(UUID workoutId);

    List<Workout> getMyWorkouts(String userEmail);

    void deleteWorkout( UUID workoutId);


    Workout updateWorkout(UUID workoutId, UpdateWorkoutRequest updateWorkoutRequest);
}
