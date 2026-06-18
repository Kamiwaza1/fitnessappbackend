package com.zeki.demo.workouthistory.repository.jpa;

import com.zeki.demo.workouthistory.entity.WorkoutHistoryEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutHistoryRepoJPA extends JpaRepository<WorkoutHistoryEntity, UUID> {
    List<WorkoutHistoryEntity> findAllByUserEmailOrderByWorkoutDateDescWorkoutIdDesc(String userEmail);

    List<WorkoutHistoryEntity> findAllByUserEmailAndWorkoutDateGreaterThanEqualOrderByWorkoutDateDescWorkoutIdDesc(
            String userEmail,
            LocalDate since
    );

    long countByWorkoutId(UUID workoutId);

    Optional<WorkoutHistoryEntity> findByWorkoutIdAndUserEmail(UUID workoutId, String userEmail);
}
