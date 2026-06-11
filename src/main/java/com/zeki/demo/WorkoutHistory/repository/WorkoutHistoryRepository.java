package com.zeki.demo.WorkoutHistory.repository;

import com.zeki.demo.WorkoutHistory.domain.WorkoutHistory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutHistoryRepository {
    List<WorkoutHistory> findAllByUserEmail(String userEmail);

    List<WorkoutHistory> findAllByUserEmailSince(String userEmail, java.time.LocalDate since);

    long countByWorkoutId(UUID workoutId);

    Optional<WorkoutHistory> getByWorkoutIdAndUserEmail(UUID workoutId, String userEmail);
}
