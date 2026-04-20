package com.zeki.demo.workouts.repository.jpa;

import com.zeki.demo.workouts.entity.Workout;
import com.zeki.demo.workouts.repository.WorkoutRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class WorkoutRepositoryJpaAdapter implements WorkoutRepository {

    private final WorkoutRepoJPA workoutRepoJPA;

    public WorkoutRepositoryJpaAdapter(WorkoutRepoJPA workoutRepoJPA) {
        this.workoutRepoJPA = workoutRepoJPA;
    }

    @Override
    public Workout save(Workout workout) {
        return workoutRepoJPA.save(workout);
    }

    @Override
    public void delete(Workout workout) {
        workoutRepoJPA.delete(workout);
    }

    @Override
    public List<Workout> findAllByUserEmailOrderByWorkoutDateDescIdDesc(String email) {
        return workoutRepoJPA.findAllByUserEmailOrderByWorkoutDateDescIdDesc(email);
    }

    @Override
    public Optional<Workout> findByIdAndUserEmail(UUID id, String email) {
        return workoutRepoJPA.findByIdAndUserEmail(id, email);
    }
}
