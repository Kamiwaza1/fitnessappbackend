package com.zeki.demo.workouts.mapperEntityDomain;

import com.zeki.demo.users.entity.UserEntity;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.entity.WorkoutEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkoutEntityDomain {

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
                .notes(workout.getNotes())
                .userEmail(workout.getUserEmail())
                .user(user)
                .build();
    }

    public Workout toDomain(WorkoutEntity workoutEntity) {
        return new Workout(
                workoutEntity.getWorkoutName(),
                workoutEntity.getWorkoutDate(),
                workoutEntity.getDurationMinutes(),
                workoutEntity.getNotes(),
                workoutEntity.getWorkoutId(),
                workoutEntity.getDescription(),
                workoutEntity.getCategory(),
                workoutEntity.getCreatedAt(),
                workoutEntity.getUpdatedAt(),
                workoutEntity.getUserEmail()
        );
    }
}
