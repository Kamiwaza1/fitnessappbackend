package com.zeki.demo.workouts.mapperdtodomain;

import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.domain.WorkoutExercise;
import com.zeki.demo.workouts.dto.CreateWorkoutRequest;
import com.zeki.demo.workouts.dto.UpdateWorkoutRequest;
import com.zeki.demo.workouts.dto.WorkoutExerciseRequest;
import com.zeki.demo.workouts.dto.WorkoutExerciseResponse;
import com.zeki.demo.workouts.dto.WorkoutResponse;
import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.enums.WorkoutDifficulty;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class WorkoutDtoDomain {

    private static final WorkoutCategory DEFAULT_CATEGORY = WorkoutCategory.GENERAL;
    private static final MuscleType DEFAULT_MUSCLE_TYPE = MuscleType.FULL_BODY;
    private static final WorkoutDifficulty DEFAULT_DIFFICULTY = WorkoutDifficulty.BEGINNER;

    public Workout toDomain(CreateWorkoutRequest request, String userEmail) {
        LocalDate now = LocalDate.now();
        List<WorkoutExercise> exercises = toExercises(request.exercises());
        return new Workout(
                request.workoutName(),
                request.workoutDate(),
                request.durationMinutes(),
                resolveSets(exercises, request.sets()),
                resolveReps(exercises, request.reps()),
                resolveWeight(exercises, request.weightKg()),
                request.notes(),
                null,
                request.description(),
                request.category() != null ? request.category() : DEFAULT_CATEGORY,
                request.muscleType() != null ? request.muscleType() : DEFAULT_MUSCLE_TYPE,
                request.difficulty() != null ? request.difficulty() : DEFAULT_DIFFICULTY,
                false,
                exercises,
                now,
                now,
                userEmail
        );
    }

    public Workout toUpdatedDomain(UpdateWorkoutRequest request, Workout existingWorkout) {
        List<WorkoutExercise> exercises = request.exercises() != null
                ? toExercises(request.exercises())
                : existingWorkout.getExercises();
        return new Workout(
                request.workoutName() != null ? request.workoutName() : existingWorkout.getWorkoutName(),
                request.workoutDate() != null ? request.workoutDate() : existingWorkout.getWorkoutDate(),
                request.durationMinutes() != null ? request.durationMinutes() : existingWorkout.getDurationMinutes(),
                resolveSets(exercises, request.sets() != null ? request.sets() : existingWorkout.getSets()),
                resolveReps(exercises, request.reps() != null ? request.reps() : existingWorkout.getReps()),
                resolveWeight(exercises, request.weightKg() != null ? request.weightKg() : existingWorkout.getWeightKg()),
                request.notes() != null ? request.notes() : existingWorkout.getNotes(),
                existingWorkout.getWorkoutId(),
                request.description() != null ? request.description() : existingWorkout.getDescription(),
                request.category() != null ? request.category() : existingWorkout.getCategory(),
                request.muscleType() != null ? request.muscleType() : existingWorkout.getMuscleType(),
                request.difficulty() != null ? request.difficulty() : existingWorkout.getDifficulty(),
                existingWorkout.isCompleted(),
                exercises,
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
                workout.getSets(),
                workout.getReps(),
                workout.getWeightKg(),
                workout.isCompleted(),
                workout.getWorkoutName(),
                workout.getNotes(),
                workout.getDescription(),
                workout.getCategory(),
                workout.getMuscleType(),
                workout.getDifficulty(),
                workout.getExercises().stream().map(this::toExerciseResponse).toList(),
                workout.getUpdatedAt(),
                workout.getCreatedAt()
        );
    }

    private List<WorkoutExercise> toExercises(List<WorkoutExerciseRequest> requests) {
        if (requests == null) {
            return List.of();
        }
        return requests.stream()
                .map(request -> new WorkoutExercise(
                        request.name(),
                        request.muscleType() != null ? request.muscleType() : DEFAULT_MUSCLE_TYPE,
                        request.sets(),
                        request.reps(),
                        request.weightKg(),
                        request.notes()
                ))
                .toList();
    }

    private WorkoutExerciseResponse toExerciseResponse(WorkoutExercise exercise) {
        return new WorkoutExerciseResponse(
                exercise.getName(),
                exercise.getMuscleType(),
                exercise.getSets(),
                exercise.getReps(),
                exercise.getWeightKg(),
                exercise.getNotes()
        );
    }

    private Integer resolveSets(List<WorkoutExercise> exercises, Integer fallback) {
        if (exercises == null || exercises.isEmpty()) {
            return fallback;
        }
        return exercises.stream().mapToInt(WorkoutExercise::getSets).sum();
    }

    private Integer resolveReps(List<WorkoutExercise> exercises, Integer fallback) {
        if (exercises == null || exercises.isEmpty()) {
            return fallback;
        }
        return exercises.stream().mapToInt(WorkoutExercise::getReps).sum();
    }

    private Double resolveWeight(List<WorkoutExercise> exercises, Double fallback) {
        if (exercises == null || exercises.isEmpty()) {
            return fallback;
        }
        return exercises.stream().mapToDouble(WorkoutExercise::getWeightKg).max().orElse(0.0);
    }
}
