package com.zeki.demo.workouts.repository.jpa;

import com.zeki.demo.workouts.entity.WorkoutEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutRepoJPA extends JpaRepository<WorkoutEntity, UUID> {
    List<WorkoutEntity> findAllByUserEmailOrderByWorkoutDateDescWorkoutIdDesc(String email);
    Optional<WorkoutEntity> findById(UUID WorkoutId);

}
