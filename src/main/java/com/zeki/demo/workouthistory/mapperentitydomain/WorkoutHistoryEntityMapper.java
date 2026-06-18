package com.zeki.demo.workouthistory.mapperentitydomain;

import com.zeki.demo.workouthistory.domain.WorkoutHistory;
import com.zeki.demo.workouthistory.entity.WorkoutHistoryEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkoutHistoryEntityMapper {

    public WorkoutHistoryEntity toEntity(WorkoutHistory history) {
        return WorkoutHistoryEntity.builder()
                .workoutId(history.getWorkoutId())
                .workoutName(history.getWorkoutName())
                .description(history.getDescription())
                .category(history.getCategory())
                .muscleType(history.getMuscleType())
                .difficulty(history.getDifficulty())
                .createdAt(history.getCreatedAt())
                .updatedAt(history.getUpdatedAt())
                .userEmail(history.getUserEmail())
                .workoutDate(history.getWorkoutDate())
                .durationMinutes(history.getDurationMinutes())
                .sets(history.getSets())
                .reps(history.getReps())
                .weightKg(history.getWeightKg())
                .notes(history.getNotes())
                .build();
    }

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
                entity.getDifficulty(),
                entity.getUpdatedAt(),
                entity.getCreatedAt(),
                entity.getUserEmail()
        );
    }
}
