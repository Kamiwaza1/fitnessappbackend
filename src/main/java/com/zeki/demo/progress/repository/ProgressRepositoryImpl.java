package com.zeki.demo.progress.repository;

import com.zeki.demo.progress.domain.repository.ProgressRepository;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.mapperentitydomain.WorkoutEntityMapper;
import com.zeki.demo.workouts.repository.jpa.WorkoutRepoJPA;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProgressRepositoryImpl implements ProgressRepository {

    private final WorkoutRepoJPA workoutRepoJPA;
    private final WorkoutEntityMapper workoutEntityMapper;

    @Override
    public List<Workout> findAllByUserEmail(String userEmail) {
        return workoutRepoJPA.findAllByUserEmailOrderByWorkoutDateDescWorkoutIdDesc(userEmail)
                .stream()
                .map(workoutEntityMapper::toDomain)
                .toList();
    }
}
