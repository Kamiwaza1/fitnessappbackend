package com.zeki.demo.workouts.repository.jpa;

import com.zeki.demo.workouts.entity.WorkoutEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepoJPA extends JpaRepository<WorkoutEntity, UUID> {
    List<WorkoutEntity> findAllByUserEmailOrderByWorkoutDateDescWorkoutIdDesc(String email);

    Optional<WorkoutEntity> findByWorkoutIdAndUserEmail(UUID workoutId, String userEmail);

    List<WorkoutEntity> findAllByUserEmailAndWorkoutDateGreaterThanEqualOrderByWorkoutDateAscWorkoutIdAsc(
            String userEmail,
            LocalDate currentDate
    );
}
