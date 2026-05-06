package com.zeki.demo.WorkoutHistory.repository.jpa;

import com.zeki.demo.WorkoutHistory.entity.WorkoutHistoryEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutHistoryRepoJPA extends JpaRepository<WorkoutHistoryEntity, UUID> {
    List<WorkoutHistoryEntity> findAllByUserEmailOrderByWorkoutDateDescWorkoutIdDesc(String userEmail);

    Optional<WorkoutHistoryEntity> findByWorkoutIdAndUserEmail(UUID workoutId, String userEmail);
}
