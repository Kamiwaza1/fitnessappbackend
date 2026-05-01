package com.zeki.demo.workouts.mapperEntityDomain;

import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.entity.UserEntity;
import com.zeki.demo.users.mapperDtoDomain.UserDtoDomain;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.entity.WorkoutEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkoutEntityDomain {

    private final UserDtoDomain userDtoDomain;

    public WorkoutEntityDomain(UserDtoDomain userDtoDomain) {
        this.userDtoDomain = userDtoDomain;
    }

    public WorkoutEntity toEntity(Workout workout, User user) {
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
                .user(userDtoDomain.toEntity(user))
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
