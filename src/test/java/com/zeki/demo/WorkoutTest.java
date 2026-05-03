package com.zeki.demo;

import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.dto.CreateWorkoutRequest;
import com.zeki.demo.workouts.dto.WorkoutResponse;
import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.mapperDtoDomain.WorkoutDtoDomain;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WorkoutTest {

    private final WorkoutDtoDomain workoutDtoDomain = new WorkoutDtoDomain();

    @Test
    void toDomainUsesProvidedWorkoutCategory() {
        CreateWorkoutRequest request = new CreateWorkoutRequest(
                "Leg Day",
                LocalDate.of(2026, 5, 16),
                60,
                "Heavy squats",
                "Lower body session",
                WorkoutCategory.STRENGTH,
                UUID.randomUUID()
        );

        Workout workout = workoutDtoDomain.toDomain(request, "test@example.com");

        assertEquals(WorkoutCategory.STRENGTH, workout.getCategory());
        assertEquals("test@example.com", workout.getUserEmail());
    }

    @Test
    void toDomainDefaultsCategoryToGeneral() {
        CreateWorkoutRequest request = new CreateWorkoutRequest(
                "Run",
                LocalDate.of(2026, 5, 17),
                30,
                "Easy pace",
                "Morning run",
                null,
                UUID.randomUUID()
        );

        Workout workout = workoutDtoDomain.toDomain(request, "test@example.com");

        assertEquals(WorkoutCategory.GENERAL, workout.getCategory());
    }

    @Test
    void responseExposesEnumCategory() {
        Workout workout = new Workout(
                "Leg Day",
                LocalDate.of(2026, 5, 16),
                60,
                "Heavy squats",
                UUID.randomUUID(),
                "Lower body session",
                WorkoutCategory.STRENGTH,
                LocalDate.of(2026, 4, 23),
                LocalDate.of(2026, 4, 23),
                "test@example.com"
        );

        WorkoutResponse response = workoutDtoDomain.toResponse(workout);

        assertEquals(WorkoutCategory.STRENGTH, response.category());
    }

    @Test
    void workoutRejectsNullCategory() {
        Workout workout = new Workout(
                "Leg Day",
                LocalDate.of(2026, 5, 16),
                60,
                "Heavy squats",
                UUID.randomUUID(),
                "Lower body session",
                null,
                LocalDate.of(2026, 4, 23),
                LocalDate.of(2026, 4, 23),
                "test@example.com"
        );

        assertThrows(IllegalArgumentException.class, workout::validate);
    }
}
