package com.zeki.demo.workouts.service;

import com.zeki.demo.workouts.dto.CreateWorkoutRequest;
import com.zeki.demo.workouts.dto.UpdateWorkoutRequest;
import com.zeki.demo.workouts.dto.WorkoutResponse;
import com.zeki.demo.users.entity.User;
import com.zeki.demo.workouts.entity.Workout;
import com.zeki.demo.workouts.mapper.WorkoutMapper;
import com.zeki.demo.users.repository.UserRepository;
import com.zeki.demo.workouts.repository.WorkoutRepository;
import com.zeki.demo.workouts.service.interfaces.IWorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutService implements IWorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final WorkoutMapper workoutMapper;

    @Override
    public WorkoutResponse createWorkout(String userEmail, CreateWorkoutRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Workout workout = workoutMapper.toEntity(request, user);

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
    public WorkoutResponse getWorkoutById(String userEmail, Long workoutId) {
        Workout workout = workoutRepository.findByIdAndUserEmail(workoutId, userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout not found"));

        return workoutMapper.toResponse(workout);
    }

    @Override
    public WorkoutResponse updateWorkout(String userEmail, Long workoutId, UpdateWorkoutRequest request) {
        Workout workout = workoutRepository.findByIdAndUserEmail(workoutId, userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout not found"));

        workoutMapper.updateEntity(workout, request);

        Workout updatedWorkout = workoutRepository.save(workout);

        return workoutMapper.toResponse(updatedWorkout);
    }

    @Override
    public void deleteWorkout(String userEmail, Long workoutId) {
        Workout workout = workoutRepository.findByIdAndUserEmail(workoutId, userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout not found"));

        workoutRepository.delete(workout);
    }

    public String getDashboardData(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDashboardData'");
    }
}
