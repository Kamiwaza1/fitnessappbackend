package com.zeki.demo.workouts.service;

import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.dto.UpdateWorkoutRequest;
import com.zeki.demo.workouts.mapperdtodomain.WorkoutDtoDomain;
import com.zeki.demo.workouts.domain.repository.WorkoutRepository;
import com.zeki.demo.workouts.exception.WorkoutNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {
    private final WorkoutRepository  workoutRepository;
    private final WorkoutDtoDomain workoutDtoDomain;

    @Override
    public Workout createWorkout(Workout workout) {
        workout.validate();
        return workoutRepository.save(workout);
    }

    @Override
    public Workout getWorkoutById(UUID workoutId, String userEmail) {
        return workoutRepository.getWorkoutByIdAndUserEmail(workoutId, userEmail)
                .orElseThrow(WorkoutNotFoundException::new);
    }

    @Override
    public List<Workout> getMyWorkouts(String userEmail) {
        return workoutRepository.findAllByUserEmail(userEmail);
    }

    @Override
    public List<Workout> upcomingWorkouts(String userEmail) {
        return workoutRepository.upcomingWorkouts(userEmail);
    }

    @Override
    public Workout updateWorkout(UUID workoutId, UpdateWorkoutRequest request, String userEmail) {
        Workout existingWorkout = workoutRepository.getWorkoutByIdAndUserEmail(workoutId, userEmail)
                .orElseThrow(WorkoutNotFoundException::new);
        Workout updatedWorkout = workoutDtoDomain.toUpdatedDomain(request, existingWorkout);
        updatedWorkout.validate();
        return workoutRepository.save(updatedWorkout);
    }

    @Override
    public void deleteWorkout(UUID workoutId, String userEmail) {
        workoutRepository.getWorkoutByIdAndUserEmail(workoutId, userEmail)
                .orElseThrow(WorkoutNotFoundException::new);
        workoutRepository.delete(workoutId);
    }
}
