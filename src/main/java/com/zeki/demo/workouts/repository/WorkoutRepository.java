package com.zeki.demo.workouts.repository;

import com.zeki.demo.workouts.entity.Workout;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutRepository {
    Workout save(Workout workout);
    void delete(Workout workout);
    List<Workout> findAllByUserEmailOrderByWorkoutDateDescIdDesc(String email);
    Optional<Workout> findByIdAndUserEmail(UUID id, String email);
}
