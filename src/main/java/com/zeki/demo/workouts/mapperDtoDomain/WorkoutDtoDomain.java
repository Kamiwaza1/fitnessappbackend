package com.zeki.demo.workouts.mapperDtoDomain;

import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.dto.CreateWorkoutRequest;
import com.zeki.demo.workouts.dto.WorkoutResponse;
import com.zeki.demo.workouts.enums.WorkoutCategory;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class WorkoutDtoDomain {

    public static final WorkoutCategory DEFAULT_CATEGORY = WorkoutCategory.GENERAL;

    public WorkoutResponse toResponse(Workout workout) {
        return WorkoutResponse.builder()
                .workoutId(workout.getWorkoutId())
                .workoutDate(workout.getWorkoutDate())
                .durationMinutes(workout.getDurationMinutes())
                .workoutName(workout.getWorkoutName())
                .notes(workout.getNotes())
                .description(workout.getDescription())
                .category(workout.getCategory())
                .updatedAt(workout.getUpdatedAt())
                .createdAt(workout.getCreatedAt())
                .userEmail(workout.getUserEmail())
                .build();
    }

    public Workout toDomain(CreateWorkoutRequest createWorkoutRequest, String userEmail) {
        LocalDate now = LocalDate.now();

        return new Workout(
                createWorkoutRequest.getWorkoutName(),
                createWorkoutRequest.getWorkoutDate(),
                createWorkoutRequest.getDurationMinutes(),
                createWorkoutRequest.getNotes(),
                createWorkoutRequest.getWorkoutId(),
                createWorkoutRequest.getDescription(),
                createWorkoutRequest.getCategory() != null ? createWorkoutRequest.getCategory() : DEFAULT_CATEGORY,
                now,
                now,
                userEmail
        );
    }
}
