package com.zeki.demo.workouts.repository;

import com.zeki.demo.workouts.domain.Workout;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutRepository {

    Workout save(String userEmail, UUID workoutId);


    Workout save(String userEmail, Workout workout);

    Workout update(UUID workoutId);

    Workout save(String userEmail);

    Workout save(Workout workout, String userEmail);

    Optional<Workout> getWorkoutById(UUID workoutId);

    List<Workout> findAllByUserEmail(String userEmail);


    void delete(UUID workoutId);
}
