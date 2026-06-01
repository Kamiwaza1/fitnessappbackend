package com.zeki.demo.workouts.repository;

import com.zeki.demo.workouts.domain.Workout;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutRepository {

    Optional<Workout> getWorkoutByIdAndUserEmail(UUID workoutId, String userEmail);

    Workout save(Workout workout);

    List<Workout> findAllByUserEmail(String userEmail);

    void delete(UUID workoutId);

    List<Workout> upcomingWorkouts(String userEmail);
}
