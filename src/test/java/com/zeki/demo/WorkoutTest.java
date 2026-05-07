package com.zeki.demo;

import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.dto.CreateWorkoutRequest;
import com.zeki.demo.workouts.dto.WorkoutResponse;
import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.mapperDtoDomain.WorkoutDtoDomain;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WorkoutTest {

    private final WorkoutDtoDomain workoutDtoDomain = new WorkoutDtoDomain();
    private static final LocalDate FIXED_DATE = LocalDate.of(2026, 5, 16);
    private static final LocalDate FIXED_CREATED_AT = LocalDate.of(2026, 4, 23);

    @Test
    void toDomainUsesProvidedWorkoutCategory() {
        CreateWorkoutRequest request = createWorkoutRequest(
                "Leg Day",
                WorkoutCategory.STRENGTH,
                MuscleType.LEGS
        );

        Workout workout = workoutDtoDomain.toDomain(request, "test@example.com");

        assertEquals(WorkoutCategory.STRENGTH, workout.getCategory());
        assertEquals(MuscleType.LEGS, workout.getMuscleType());
        assertEquals("test@example.com", workout.getUserEmail());
        assertEquals(4, workout.getSets());
        assertEquals(10, workout.getReps());
    }

    @Test
    void toDomainDefaultsCategoryToGeneral() {
        CreateWorkoutRequest request = createWorkoutRequest(
                "Run",
                null,
                null
        );

        Workout workout = workoutDtoDomain.toDomain(request, "test@example.com");

        assertEquals(WorkoutCategory.GENERAL, workout.getCategory());
        assertEquals(MuscleType.FULL_BODY, workout.getMuscleType());
    }

    @Test
    void responseExposesEnumCategory() {
        Workout workout = createWorkout(
                "Leg Day",
                WorkoutCategory.STRENGTH,
                MuscleType.LEGS
        );

        WorkoutResponse response = workoutDtoDomain.toResponse(workout);

        assertEquals(WorkoutCategory.STRENGTH, response.category());
        assertEquals(MuscleType.LEGS, response.muscleType());
        assertEquals(5, response.sets());
        assertEquals(8, response.reps());
    }

    @Test
    void workoutRejectsNullCategory() {
        Workout workout = createWorkout("Leg Day", null, MuscleType.LEGS);

        assertThrows(IllegalArgumentException.class, workout::validate);
    }

    private CreateWorkoutRequest createWorkoutRequest(
            String workoutName,
            WorkoutCategory category,
            MuscleType muscleType
    ) {
        return new CreateWorkoutRequest(
                workoutName,
                FIXED_DATE,
                60,
                4,
                10,
                100.0,
                "Heavy squats",
                "Lower body session",
                category,
                muscleType,
                null,
                UUID.randomUUID()
        );
    }

    private Workout createWorkout(
            String workoutName,
            WorkoutCategory category,
            MuscleType muscleType
    ) {
        return new Workout(
                workoutName,
                FIXED_DATE,
                60,
                5,
                8,
                120.0,
                "Heavy squats",
                UUID.randomUUID(),
                "Lower body session",
                category,
                muscleType,
                null,
                FIXED_CREATED_AT,
                FIXED_CREATED_AT,
                "test@example.com"
        );
    }
}
