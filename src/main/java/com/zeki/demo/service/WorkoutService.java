package com.zeki.demo.service;

import com.zeki.demo.dto.CreateWorkoutRequest;
import com.zeki.demo.dto.UpdateWorkoutRequest;
import com.zeki.demo.dto.WorkoutResponse;
import com.zeki.demo.entity.User;
import com.zeki.demo.entity.Workout;
import com.zeki.demo.repository.UserRepository;
import com.zeki.demo.repository.WorkoutRepository;
import com.zeki.demo.service.Interface.IWorkoutService;
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

    @Override
    public WorkoutResponse createWorkout(String userEmail, CreateWorkoutRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Workout workout = Workout.builder()
                .name(request.getName())
                .workoutDate(request.getWorkoutDate())
                .durationMinutes(request.getDurationMinutes())
                .notes(request.getNotes())
                .user(user)
                .build();

        Workout savedWorkout = workoutRepository.save(workout);

        return toResponse(savedWorkout);
    }

    @Override
    public List<WorkoutResponse> getWorkouts(String userEmail) {
        return workoutRepository.findAllByUserEmailOrderByWorkoutDateDescIdDesc(userEmail)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public WorkoutResponse getWorkoutById(String userEmail, Long workoutId) {
        Workout workout = workoutRepository.findByIdAndUserEmail(workoutId, userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout not found"));

        return toResponse(workout);
    }

    @Override
    public WorkoutResponse updateWorkout(String userEmail, Long workoutId, UpdateWorkoutRequest request) {
        Workout workout = workoutRepository.findByIdAndUserEmail(workoutId, userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout not found"));

        workout.setName(request.getName());
        workout.setWorkoutDate(request.getWorkoutDate());
        workout.setDurationMinutes(request.getDurationMinutes());
        workout.setNotes(request.getNotes());

        Workout updatedWorkout = workoutRepository.save(workout);

        return toResponse(updatedWorkout);
    }

    @Override
    public void deleteWorkout(String userEmail, Long workoutId) {
        Workout workout = workoutRepository.findByIdAndUserEmail(workoutId, userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout not found"));

        workoutRepository.delete(workout);
    }

    private WorkoutResponse toResponse(Workout workout) {
        return new WorkoutResponse(
                workout.getId(),
                workout.getName(),
                workout.getWorkoutDate(),
                workout.getDurationMinutes(),
                workout.getNotes()
        );
    }
}
