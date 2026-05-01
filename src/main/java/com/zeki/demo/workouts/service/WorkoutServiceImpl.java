package com.zeki.demo.workouts.service;

import com.zeki.demo.workouts.WorkoutException.WorkoutExistException;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.domain.WorkoutValidator;
import com.zeki.demo.workouts.dto.CreateWorkoutRequest;
import com.zeki.demo.workouts.dto.UpdateWorkoutRequest;
import com.zeki.demo.workouts.mapperDtoDomain.WorkoutDtoDomain;
import com.zeki.demo.workouts.repository.WorkoutRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;


@Service
@Transactional
public class WorkoutServiceImpl implements WorkoutService{
    private final WorkoutRepository workoutRepository;
    private final WorkoutDtoDomain workoutDtoDomain;
    private final WorkoutValidator workoutValidator;

    public WorkoutServiceImpl(WorkoutRepository workoutRepository, WorkoutDtoDomain workoutDtoDomain, WorkoutValidator workoutValidator) {

        this.workoutRepository = workoutRepository;

        this.workoutDtoDomain = workoutDtoDomain;
        this.workoutValidator = workoutValidator;
    }


    @Override
    public Workout createWorkout(Workout workout) {
        workoutValidator.validateWorkout(workout);
        return workoutRepository.save(workout);
    }

    public Workout getWorkoutById(UUID workoutId) {

        return workoutRepository.getWorkoutById(workoutId)

                .orElseThrow(() -> new WorkoutExistException("Workout not found"));

    }

    public List<Workout> getMyWorkouts(String userEmail) {

        return workoutRepository.findAllByUserEmail(userEmail);

    }

    @Override
    public Workout updateWorkout(UUID workoutId, UpdateWorkoutRequest updateWorkoutRequest) {
        Workout workout = workoutRepository.getWorkoutById(workoutId)
                .orElseThrow(() -> new WorkoutExistException("Workout not found"));

        if (!workout.getWorkoutId().equals(workoutId)) {
            throw new WorkoutExistException("Workout not found");
        }

        workout.markUpdated();

        return workoutRepository.save(workout);
    }

    public void deleteWorkout(UUID workoutId) {

        Workout workout = workoutRepository.getWorkoutById(workoutId)

                .orElseThrow(() -> new WorkoutExistException("Workout not found"));

        workoutRepository.delete(workoutId);

    }

}