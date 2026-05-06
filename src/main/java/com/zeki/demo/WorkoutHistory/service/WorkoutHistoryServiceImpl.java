package com.zeki.demo.WorkoutHistory.service;

import com.zeki.demo.WorkoutHistory.domain.WorkoutHistory;
import com.zeki.demo.WorkoutHistory.repository.WorkoutHistoryRepository;
import com.zeki.demo.workouts.WorkoutException.WorkoutExistException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkoutHistoryServiceImpl implements WorkoutHistoryService {

    private final WorkoutHistoryRepository workoutHistoryRepository;

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
