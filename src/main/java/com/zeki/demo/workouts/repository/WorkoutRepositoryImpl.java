package com.zeki.demo.workouts.repository;

import com.zeki.demo.common.exception.NotFoundException;
import com.zeki.demo.users.entity.UserEntity;
import com.zeki.demo.users.repository.JPA.UserRepoJPA;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.entity.WorkoutEntity;
import com.zeki.demo.workouts.mapperEntityDomain.WorkoutEntityMapper;
import com.zeki.demo.workouts.repository.jpa.WorkoutRepoJPA;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WorkoutRepositoryImpl implements WorkoutRepository {

    private final WorkoutRepoJPA workoutRepoJPA;
    private final WorkoutEntityMapper workoutEntityMapper;
    private final UserRepoJPA userRepoJPA;

    @Override
    public Workout save(Workout workout) {
        UserEntity user = userRepoJPA.findByEmailIgnoreCaseAndIsActiveTrue(workout.getUserEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));
        WorkoutEntity savedEntity = workoutRepoJPA.save(workoutEntityMapper.toEntity(workout, user));
        return workoutEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Workout> getWorkoutById(UUID workoutId) {
        return workoutRepoJPA.findById(workoutId).map(workoutEntityMapper::toDomain);
    }

    @Override
    public List<Workout> findAllByUserEmail(String userEmail) {
        return workoutRepoJPA.findAllByUserEmailOrderByWorkoutDateDescWorkoutIdDesc(userEmail)
                .stream()
                .map(workoutEntityMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(UUID workoutId) {
        WorkoutEntity workoutEntity = workoutRepoJPA.findById(workoutId)
                .orElseThrow(() -> new NotFoundException("Workout not found"));
        workoutRepoJPA.delete(workoutEntity);
    }

    @Override
    public boolean isOwnedByUser(UUID workoutId, String userEmail) {
        return workoutRepoJPA.existsByIdAndUserEmail(workoutId, userEmail);
    }
}
