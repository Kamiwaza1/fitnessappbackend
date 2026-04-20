package com.zeki.demo.workouts.repository.jpa;

import com.zeki.demo.workouts.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutRepoJPA extends JpaRepository<Workout, UUID> {
    List<Workout> findAllByUserEmailOrderByWorkoutDateDescIdDesc(String email);
    Optional<Workout> findByIdAndUserEmail(UUID id, String email);
}
