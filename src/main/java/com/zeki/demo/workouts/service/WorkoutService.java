package com.zeki.demo.workouts.service;

import com.zeki.demo.workouts.dto.CreateWorkoutRequest;
import com.zeki.demo.workouts.dto.UpdateWorkoutRequest;
import com.zeki.demo.workouts.dto.WorkoutResponse;
import com.zeki.demo.users.entity.User;
import com.zeki.demo.common.exception.NotFoundException;
import com.zeki.demo.workouts.domain.WorkoutDomainService;
import com.zeki.demo.users.repository.UserRepository;
import com.zeki.demo.workouts.entity.Workout;
import com.zeki.demo.workouts.mapper.WorkoutMapper;
import com.zeki.demo.workouts.repository.WorkoutRepository;
import com.zeki.demo.workouts.service.interfaces.IWorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkoutService implements IWorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final WorkoutMapper workoutMapper;
    private final WorkoutDomainService workoutDomainService;

    @Override
    public WorkoutResponse createWorkout(String userEmail, CreateWorkoutRequest request) {
        User user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Workout workout = workoutDomainService.createWorkout(request, user);

        Workout savedWorkout = workoutRepository.save(workout);

        return workoutMapper.toResponse(savedWorkout);
    }

    @Override
    public List<WorkoutResponse> getWorkouts(String userEmail) {
        return workoutRepository.findAllByUserEmailOrderByWorkoutDateDescIdDesc(userEmail)
                .stream()
                .map(workoutMapper::toResponse)
                .toList();
    }

    @Override
    public WorkoutResponse getWorkoutById(String userEmail, UUID workoutId) {
        Workout workout = workoutRepository.findByIdAndUserEmail(workoutId, userEmail)
                .orElseThrow(() -> new NotFoundException("Workout not found"));

        return workoutMapper.toResponse(workout);
    }

    @Override
    public WorkoutResponse updateWorkout(String userEmail, UUID workoutId, UpdateWorkoutRequest request) {
        Workout workout = workoutRepository.findByIdAndUserEmail(workoutId, userEmail)
                .orElseThrow(() -> new NotFoundException("Workout not found"));

        workoutDomainService.updateWorkout(workout, request);

        Workout updatedWorkout = workoutRepository.save(workout);

        return workoutMapper.toResponse(updatedWorkout);
    }

    @Override
    public void deleteWorkout(String userEmail, UUID workoutId) {
        Workout workout = workoutRepository.findByIdAndUserEmail(workoutId, userEmail)
                .orElseThrow(() -> new NotFoundException("Workout not found"));

        workoutRepository.delete(workout);
    }

    @Override
    public String getDashboardData(String userEmail) {
        List<Workout> workouts = workoutRepository.findAllByUserEmailOrderByWorkoutDateDescIdDesc(userEmail);
        return workoutDomainService.buildDashboardData(workouts);
    }
}
