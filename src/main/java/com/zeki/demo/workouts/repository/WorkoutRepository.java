package com.zeki.demo.workouts.repository;

import com.zeki.demo.workouts.domain.Workout;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutRepository {

    Optional<Workout> getWorkoutById(UUID workoutId);

    Workout save(Workout workout);

    List<Workout> findAllByUserEmail(String userEmail);

    void delete(UUID workoutId);

    boolean isOwnedByUser(UUID workoutId, String userEmail);
    List<Workout> upcomingWorkouts(String userEmail);
}
