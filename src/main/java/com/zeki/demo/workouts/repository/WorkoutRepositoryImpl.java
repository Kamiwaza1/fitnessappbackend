package com.zeki.demo.workouts.repository;

import com.zeki.demo.common.exception.NotFoundException;
import com.zeki.demo.users.entity.UserEntity;
import com.zeki.demo.users.repository.UserRepository;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.entity.WorkoutEntity;
import com.zeki.demo.workouts.mapperEntityDomain.WorkoutEntityDomain;
import com.zeki.demo.workouts.repository.jpa.WorkoutRepoJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WorkoutRepositoryImpl implements WorkoutRepository {

    private final WorkoutRepoJPA workoutRepoJPA;
    private final WorkoutEntityDomain workoutEntityDomain;
    private final UserRepository userRepository;


    @Override
    public Workout save(Workout workout) {
        UserEntity user = userRepository.findByEmailIgnoreCase(workout.getUserEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

        WorkoutEntity workoutEntity = workoutEntityDomain.toEntity(workout, user);
        WorkoutEntity savedWorkoutEntity = workoutRepoJPA.save(workoutEntity);

        return workoutEntityDomain.toDomain(savedWorkoutEntity);
    }

    @Override
    public Optional<Workout> getWorkoutById(UUID workoutId) {
        return workoutRepoJPA.findById(workoutId)
                .map(workoutEntityDomain::toDomain);
    }

    @Override
    public List<Workout> findAllByUserEmail(String userEmail) {
        return workoutRepoJPA.findAllByUserEmailOrderByWorkoutDateDescIdDesc(userEmail)
                .stream()
                .map(workoutEntityDomain::toDomain)
                .toList();
    }


    @Override
    public void delete(UUID workoutId) {
        WorkoutEntity workoutEntity = workoutRepoJPA.findById(workoutId)
                .orElseThrow(() -> new NotFoundException("Workout not found"));

        workoutRepoJPA.delete(workoutEntity);
    }

}
