package com.zeki.demo.WorkoutHistory.mapperDtoDomain;

import com.zeki.demo.WorkoutHistory.domain.WorkoutHistory;
import com.zeki.demo.WorkoutHistory.dto.WorkoutHistoryResponse;
import org.springframework.stereotype.Component;

@Component
public class WorkoutHistoryDtoDomain {

    public WorkoutHistoryResponse toResponse(WorkoutHistory workoutHistory) {
        return new WorkoutHistoryResponse(
                workoutHistory.getWorkoutId(),
                workoutHistory.getWorkoutDate(),
                workoutHistory.getDurationMinutes(),
                workoutHistory.getSets(),
                workoutHistory.getReps(),
                workoutHistory.getWeightKg(),
                workoutHistory.getWorkoutName(),
                workoutHistory.getNotes(),
                workoutHistory.getDescription(),
                workoutHistory.getCategory(),
                workoutHistory.getMuscleType(),
                workoutHistory.getDifficulty(),
                workoutHistory.getUpdatedAt(),
                workoutHistory.getCreatedAt()
        );
    }
}
