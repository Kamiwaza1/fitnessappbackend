package com.zeki.demo.WorkoutHistory.service;

import com.zeki.demo.WorkoutHistory.domain.WorkoutHistory;
import com.zeki.demo.WorkoutHistory.dto.CreateWorkoutHistoryRequest;
import java.util.List;
import java.util.UUID;

public interface WorkoutHistoryService {
    WorkoutHistory createWorkoutHistory(CreateWorkoutHistoryRequest request, String userEmail);

    List<WorkoutHistory> getWorkoutHistory(String userEmail);

    WorkoutHistory getWorkoutHistoryById(UUID workoutId, String userEmail);
}
