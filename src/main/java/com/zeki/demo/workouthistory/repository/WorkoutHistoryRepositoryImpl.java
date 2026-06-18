package com.zeki.demo.workouthistory.repository;

import com.zeki.demo.workouthistory.domain.WorkoutHistory;
import com.zeki.demo.workouthistory.mapperentitydomain.WorkoutHistoryEntityMapper;
import com.zeki.demo.workouthistory.repository.jpa.WorkoutHistoryRepoJPA;
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
    public WorkoutHistory save(WorkoutHistory workoutHistory) {
        return workoutHistoryEntityMapper.toDomain(
                workoutHistoryRepoJPA.save(workoutHistoryEntityMapper.toEntity(workoutHistory))
        );
    }

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
