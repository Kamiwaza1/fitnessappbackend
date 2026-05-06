package com.zeki.demo.WorkoutHistory.service;

import com.zeki.demo.WorkoutHistory.domain.WorkoutHistory;
import java.util.List;
import java.util.UUID;

public interface WorkoutHistoryService {

    List<WorkoutHistory> getWorkoutHistory(String userEmail);

    WorkoutHistory getWorkoutHistoryById(UUID workoutId, String userEmail);
}
