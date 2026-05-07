package com.zeki.demo.workouts.mapperEntityDomain;

import com.zeki.demo.users.entity.UserEntity;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.domain.WorkoutExercise;
import com.zeki.demo.workouts.entity.WorkoutEntity;
import com.zeki.demo.workouts.entity.WorkoutExerciseValue;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class WorkoutEntityMapper {

    public WorkoutEntity toEntity(Workout workout, UserEntity user) {
        return WorkoutEntity.builder()
                .workoutId(workout.getWorkoutId())
                .workoutName(workout.getWorkoutName())
                .description(workout.getDescription())
                .category(workout.getCategory())
                .muscleType(workout.getMuscleType())
                .createdAt(workout.getCreatedAt())
                .updatedAt(workout.getUpdatedAt())
                .workoutDate(workout.getWorkoutDate())
                .durationMinutes(workout.getDurationMinutes())
                .sets(workout.getSets())
                .reps(workout.getReps())
                .weightKg(workout.getWeightKg())
                .notes(workout.getNotes())
                .exercises(
                        workout.getExercises().stream()
                                .map(this::toExerciseValue)
                                .toList()
                )
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
                entity.getMuscleType(),
                toExercises(entity.getExercises()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getUserEmail()
        );
    }

    private WorkoutExerciseValue toExerciseValue(WorkoutExercise exercise) {
        return WorkoutExerciseValue.builder()
                .name(exercise.getName())
                .muscleType(exercise.getMuscleType())
                .sets(exercise.getSets())
                .reps(exercise.getReps())
                .weightKg(exercise.getWeightKg())
                .notes(exercise.getNotes())
                .build();
    }

    private List<WorkoutExercise> toExercises(List<WorkoutExerciseValue> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream()
                .map(value -> new WorkoutExercise(
                        value.getName(),
                        value.getMuscleType(),
                        value.getSets(),
                        value.getReps(),
                        value.getWeightKg(),
                        value.getNotes()
                ))
                .toList();
    }
}
