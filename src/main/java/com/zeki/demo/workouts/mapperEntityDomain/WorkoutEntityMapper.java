package com.zeki.demo.workouts.mapperEntityDomain;

import com.zeki.demo.users.entity.UserEntity;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.entity.WorkoutEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkoutEntityMapper {

    public WorkoutEntity toEntity(Workout workout, UserEntity user) {
        return WorkoutEntity.builder()
                .workoutId(workout.getWorkoutId())
                .workoutName(workout.getWorkoutName())
                .description(workout.getDescription())
                .category(workout.getCategory())
                .createdAt(workout.getCreatedAt())
                .updatedAt(workout.getUpdatedAt())
                .workoutDate(workout.getWorkoutDate())
                .durationMinutes(workout.getDurationMinutes())
                .sets(workout.getSets())
                .reps(workout.getReps())
                .weightKg(workout.getWeightKg())
                .notes(workout.getNotes())
                .userEmail(workout.getUserEmail())
                .user(user)
                .build();
    }

    public Workout toDomain(WorkoutEntity entity) {
        return new Workout(
                entity.getWorkoutName(),
                entity.getWorkoutDate(),
                entity.getDurationMinutes(),
                entity.getSets(),
                entity.getReps(),
                entity.getWeightKg(),
                entity.getNotes(),
                entity.getWorkoutId(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getUserEmail()
        );
    }
}
