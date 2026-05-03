package com.zeki.demo.workouts.mapperDtoDomain;

import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.dto.CreateWorkoutRequest;
import com.zeki.demo.workouts.dto.UpdateWorkoutRequest;
import com.zeki.demo.workouts.dto.WorkoutResponse;
import com.zeki.demo.workouts.enums.WorkoutCategory;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class WorkoutDtoDomain {

    private static final WorkoutCategory DEFAULT_CATEGORY = WorkoutCategory.GENERAL;

    public Workout toDomain(CreateWorkoutRequest request, String userEmail) {
        LocalDate now = LocalDate.now();
        return new Workout(
                request.workoutName(),
                request.workoutDate(),
                request.durationMinutes(),
                request.notes(),
                request.workoutId(),
                request.description(),
                request.category() != null ? request.category() : DEFAULT_CATEGORY,
                now,
                now,
                userEmail
        );
    }

    public Workout toUpdatedDomain(UpdateWorkoutRequest request, Workout existingWorkout) {
        return new Workout(
                request.workoutName() != null ? request.workoutName() : existingWorkout.getWorkoutName(),
                request.workoutDate() != null ? request.workoutDate() : existingWorkout.getWorkoutDate(),
                request.durationMinutes() != null ? request.durationMinutes() : existingWorkout.getDurationMinutes(),
                request.notes() != null ? request.notes() : existingWorkout.getNotes(),
                existingWorkout.getWorkoutId(),
                request.description() != null ? request.description() : existingWorkout.getDescription(),
                request.category() != null ? request.category() : existingWorkout.getCategory(),
                existingWorkout.getCreatedAt(),
                LocalDate.now(),
                existingWorkout.getUserEmail()
        );
    }

    public WorkoutResponse toResponse(Workout workout) {
        return new WorkoutResponse(
                workout.getWorkoutId(),
                workout.getWorkoutDate(),
                workout.getDurationMinutes(),
                workout.getWorkoutName(),
                workout.getNotes(),
                workout.getDescription(),
                workout.getCategory(),
                workout.getUpdatedAt(),
                workout.getCreatedAt()
        );
    }
}
