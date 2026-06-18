package com.zeki.demo.workouthistory.service;

import com.zeki.demo.workouthistory.domain.WorkoutHistory;
import com.zeki.demo.workouthistory.dto.CreateWorkoutHistoryRequest;
import java.util.List;
import java.util.UUID;

public interface WorkoutHistoryService {
    WorkoutHistory createWorkoutHistory(CreateWorkoutHistoryRequest request, String userEmail);

    List<WorkoutHistory> getWorkoutHistory(String userEmail);

    WorkoutHistory getWorkoutHistoryById(UUID workoutId, String userEmail);
}
