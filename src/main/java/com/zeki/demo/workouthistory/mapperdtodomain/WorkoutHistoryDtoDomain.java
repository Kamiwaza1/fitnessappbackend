package com.zeki.demo.WorkoutHistory.mapperDtoDomain;

import com.zeki.demo.WorkoutHistory.domain.WorkoutHistory;
import com.zeki.demo.WorkoutHistory.dto.CreateWorkoutHistoryRequest;
import com.zeki.demo.WorkoutHistory.dto.WorkoutHistoryResponse;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.enums.WorkoutDifficulty;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class WorkoutHistoryDtoDomain {

    public WorkoutHistory toDomain(CreateWorkoutHistoryRequest request, String userEmail, Workout sourceWorkout) {
        LocalDate now = LocalDate.now();
        return new WorkoutHistory(
                request.workoutId(),
                request.workoutDate() != null ? request.workoutDate() : now,
                request.durationMinutes() != null ? request.durationMinutes() : resolveDuration(sourceWorkout),
                request.sets() != null ? Math.max(1, request.sets()) : 1,
                request.reps() != null ? Math.max(1, request.reps()) : 1,
                request.weightKg() != null ? Math.max(0, request.weightKg()) : 0,
                request.workoutName(),
                request.notes(),
                request.description() != null ? request.description() : resolveDescription(sourceWorkout),
                request.category() != null ? request.category() : resolveCategory(sourceWorkout),
                request.muscleType() != null ? request.muscleType() : resolveMuscleType(request, sourceWorkout),
                request.difficulty() != null ? request.difficulty() : resolveDifficulty(sourceWorkout),
                now,
                sourceWorkout != null && sourceWorkout.getCreatedAt() != null ? sourceWorkout.getCreatedAt() : now,
                userEmail
        );
    }

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

    private Integer resolveDuration(Workout sourceWorkout) {
        return sourceWorkout != null && sourceWorkout.getDurationMinutes() != null
                ? sourceWorkout.getDurationMinutes()
                : 1;
    }

    private String resolveDescription(Workout sourceWorkout) {
        return sourceWorkout == null ? null : sourceWorkout.getDescription();
    }

    private WorkoutCategory resolveCategory(Workout sourceWorkout) {
        return sourceWorkout != null && sourceWorkout.getCategory() != null
                ? sourceWorkout.getCategory()
                : WorkoutCategory.GENERAL;
    }

    private MuscleType resolveMuscleType(CreateWorkoutHistoryRequest request, Workout sourceWorkout) {
        if (sourceWorkout != null && sourceWorkout.getMuscleType() != null) {
            return sourceWorkout.getMuscleType();
        }
        if (request.exercises() != null) {
            return request.exercises()
                    .stream()
                    .filter(exercise -> exercise.muscleType() != null)
                    .map(exercise -> exercise.muscleType())
                    .findFirst()
                    .orElse(MuscleType.FULL_BODY);
        }
        return MuscleType.FULL_BODY;
    }

    private WorkoutDifficulty resolveDifficulty(Workout sourceWorkout) {
        return sourceWorkout != null && sourceWorkout.getDifficulty() != null
                ? sourceWorkout.getDifficulty()
                : WorkoutDifficulty.BEGINNER;
    }
}
