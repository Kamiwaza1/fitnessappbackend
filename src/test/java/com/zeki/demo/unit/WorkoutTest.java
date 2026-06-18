package com.zeki.demo.unit;

import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.dto.CreateWorkoutRequest;
import com.zeki.demo.workouts.dto.WorkoutResponse;
import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.mapperdtodomain.WorkoutDtoDomain;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WorkoutTest {
    //mapper test no need for mocks

    private final WorkoutDtoDomain workoutDtoDomain = new WorkoutDtoDomain();

    private static final String TEST_EMAIL = "test@example.com";
    private static final String DEFAULT_NAME = "Leg Day";
    private static final String DEFAULT_NOTES = "Heavy squats";
    private static final String DEFAULT_DESCRIPTION = "Lower body session";
    private static final LocalDate FIXED_WORKOUT_DATE = LocalDate.of(2026, 5, 16);
    private static final LocalDate FIXED_CREATED_AT = LocalDate.of(2026, 4, 23);

    @Test
    void toDomain_shouldMapCreateWorkoutRequestToWorkout_whenCategoryAndMuscleTypeAreProvided() {
        CreateWorkoutRequest request = createWorkoutRequest(WorkoutCategory.STRENGTH, MuscleType.LEGS);

        Workout workout = workoutDtoDomain.toDomain(request, TEST_EMAIL);

        assertEquals(DEFAULT_NAME, workout.getWorkoutName());
        assertEquals(FIXED_WORKOUT_DATE, workout.getWorkoutDate());
        assertEquals(60, workout.getDurationMinutes());
        assertEquals(4, workout.getSets());
        assertEquals(10, workout.getReps());
        assertEquals(100.0, workout.getWeightKg());
        assertEquals(DEFAULT_NOTES, workout.getNotes());
        assertEquals(DEFAULT_DESCRIPTION, workout.getDescription());
        assertEquals(WorkoutCategory.STRENGTH, workout.getCategory());
        assertEquals(MuscleType.LEGS, workout.getMuscleType());
        assertEquals(TEST_EMAIL, workout.getUserEmail());
    }

    @Test
    void toDomain_shouldUseDefaultCategoryAndMuscleType_whenRequestDoesNotProvideThem() {
        CreateWorkoutRequest request = createWorkoutRequest(null, null);

        Workout workout = workoutDtoDomain.toDomain(request, TEST_EMAIL);

        assertEquals(WorkoutCategory.GENERAL, workout.getCategory());
        assertEquals(MuscleType.FULL_BODY, workout.getMuscleType());
    }

    @Test
    void toResponse_shouldMapWorkoutToWorkoutResponse() {
        Workout workout = createWorkout(WorkoutCategory.STRENGTH, MuscleType.LEGS);

        WorkoutResponse response = workoutDtoDomain.toResponse(workout);

        assertEquals(DEFAULT_NAME, response.workoutName());
        assertEquals(FIXED_WORKOUT_DATE, response.workoutDate());
        assertEquals(60, response.durationMinutes());
        assertEquals(5, response.sets());
        assertEquals(8, response.reps());
        assertEquals(120.0, response.weightKg());
        assertEquals(DEFAULT_NOTES, response.notes());
        assertEquals(DEFAULT_DESCRIPTION, response.description());
        assertEquals(WorkoutCategory.STRENGTH, response.category());
        assertEquals(MuscleType.LEGS, response.muscleType());
    }

    @Test
    void validate_shouldThrowException_whenWorkoutCategoryIsNull() {
        Workout workout = createWorkout(null, MuscleType.LEGS);

        assertThrows(IllegalArgumentException.class, workout::validate);
    }

    private CreateWorkoutRequest createWorkoutRequest(WorkoutCategory category, MuscleType muscleType) {
        return new CreateWorkoutRequest(
                DEFAULT_NAME,
                FIXED_WORKOUT_DATE,
                60,
                4,
                10,
                100.0,
                DEFAULT_NOTES,
                DEFAULT_DESCRIPTION,
                category,
                muscleType,
                null,
                UUID.randomUUID()
        );
    }

    private Workout createWorkout(WorkoutCategory category, MuscleType muscleType) {
        return new Workout(
                DEFAULT_NAME,
                FIXED_WORKOUT_DATE,
                60,
                5,
                8,
                120.0,
                DEFAULT_NOTES,
                UUID.randomUUID(),
                DEFAULT_DESCRIPTION,
                category,
                muscleType,
                null,
                FIXED_CREATED_AT,
                FIXED_CREATED_AT,
                TEST_EMAIL
        );
    }
}
