package com.zeki.demo.repository;

import com.zeki.demo.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    List<Workout> findAllByUserEmailOrderByWorkoutDateDescIdDesc(String email);
    Optional<Workout> findByIdAndUserEmail(Long id, String email);
}
