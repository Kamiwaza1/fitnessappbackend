package com.zeki.demo.WorkoutHistory.service;

import com.zeki.demo.WorkoutHistory.domain.WorkoutHistory;
import com.zeki.demo.WorkoutHistory.dto.CreateWorkoutHistoryRequest;
import com.zeki.demo.WorkoutHistory.mapperDtoDomain.WorkoutHistoryDtoDomain;
import com.zeki.demo.WorkoutHistory.repository.WorkoutHistoryRepository;
import com.zeki.demo.workouts.WorkoutException.WorkoutExistException;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.repository.WorkoutRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkoutHistoryServiceImpl implements WorkoutHistoryService {

    private final WorkoutHistoryRepository workoutHistoryRepository;
    private final WorkoutHistoryDtoDomain workoutHistoryDtoDomain;
    private final WorkoutRepository workoutRepository;

    @Override
    public WorkoutHistory createWorkoutHistory(CreateWorkoutHistoryRequest request, String userEmail) {
        Optional<Workout> sourceWorkout = workoutRepository.getWorkoutByIdAndUserEmail(request.workoutId(), userEmail);
        WorkoutHistory workoutHistory = workoutHistoryDtoDomain.toDomain(request, userEmail, sourceWorkout.orElse(null));
        WorkoutHistory savedHistory = workoutHistoryRepository.save(workoutHistory);
        sourceWorkout.ifPresent(workout -> workoutRepository.save(workout.markCompleted()));
        return savedHistory;
    }

    @Override
    public List<WorkoutHistory> getWorkoutHistory(String userEmail) {
        return workoutHistoryRepository.findAllByUserEmail(userEmail);
    }

    @Override
    public WorkoutHistory getWorkoutHistoryById(UUID workoutId, String userEmail) {
        return workoutHistoryRepository.getByWorkoutIdAndUserEmail(workoutId, userEmail)
                .orElseThrow(() -> new WorkoutExistException("Workout not found"));
    }
}
