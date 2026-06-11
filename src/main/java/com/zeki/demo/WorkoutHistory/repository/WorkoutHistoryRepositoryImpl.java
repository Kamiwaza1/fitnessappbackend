package com.zeki.demo.WorkoutHistory.repository;

import com.zeki.demo.WorkoutHistory.domain.WorkoutHistory;
import com.zeki.demo.WorkoutHistory.mapperEntityDomain.WorkoutHistoryEntityMapper;
import com.zeki.demo.WorkoutHistory.repository.jpa.WorkoutHistoryRepoJPA;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WorkoutHistoryRepositoryImpl implements WorkoutHistoryRepository {

    private final WorkoutHistoryRepoJPA workoutHistoryRepoJPA;
    private final WorkoutHistoryEntityMapper workoutHistoryEntityMapper;

    @Override
    public List<WorkoutHistory> findAllByUserEmail(String userEmail) {
        return workoutHistoryRepoJPA.findAllByUserEmailOrderByWorkoutDateDescWorkoutIdDesc(userEmail)
                .stream()
                .map(workoutHistoryEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<WorkoutHistory> findAllByUserEmailSince(String userEmail, java.time.LocalDate since) {
        return workoutHistoryRepoJPA.findAllByUserEmailAndWorkoutDateGreaterThanEqualOrderByWorkoutDateDescWorkoutIdDesc(userEmail, since)
                .stream()
                .map(workoutHistoryEntityMapper::toDomain)
                .toList();
    }

    @Override
    public long countByWorkoutId(UUID workoutId) {
        return workoutHistoryRepoJPA.countByWorkoutId(workoutId);
    }

    @Override
    public Optional<WorkoutHistory> getByWorkoutIdAndUserEmail(UUID workoutId, String userEmail) {
        return workoutHistoryRepoJPA.findByWorkoutIdAndUserEmail(workoutId, userEmail)
                .map(workoutHistoryEntityMapper::toDomain);
    }
}
