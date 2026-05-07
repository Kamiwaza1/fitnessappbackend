package com.zeki.demo.WorkoutHistory.mapperEntityDomain;

import com.zeki.demo.WorkoutHistory.domain.WorkoutHistory;
import com.zeki.demo.WorkoutHistory.entity.WorkoutHistoryEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkoutHistoryEntityMapper {

    public WorkoutHistory toDomain(WorkoutHistoryEntity entity) {
        return new WorkoutHistory(
                entity.getWorkoutId(),
                entity.getWorkoutDate(),
                entity.getDurationMinutes(),
                entity.getSets(),
                entity.getReps(),
                entity.getWeightKg(),
                entity.getWorkoutName(),
                entity.getNotes(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getMuscleType(),
                entity.getUpdatedAt(),
                entity.getCreatedAt(),
                entity.getUserEmail()
        );
    }
}
