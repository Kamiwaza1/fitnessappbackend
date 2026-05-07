package com.zeki.demo.Unit;

import com.zeki.demo.workouts.domain.WorkoutExercise;
import com.zeki.demo.workouts.enums.MuscleType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WorkoutExerciseTest {

    @Test
    void validateAcceptsTrimmedValidExercise() {
        WorkoutExercise exercise = new WorkoutExercise("  Pull Up  ", MuscleType.BACK, 4, 10, 0.0, "  strict  ");

        exercise.validate();

        assertEquals("Pull Up", exercise.getName());
        assertEquals("strict", exercise.getNotes());
    }

    @Test
    void validateRejectsBlankName() {
        WorkoutExercise exercise = new WorkoutExercise(" ", MuscleType.BACK, 4, 10, 0.0, null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, exercise::validate);

        assertEquals("Exercise name is required", ex.getMessage());
    }

    @Test
    void validateRejectsMissingMuscleType() {
        WorkoutExercise exercise = new WorkoutExercise("Row", null, 4, 10, 20.0, null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, exercise::validate);

        assertEquals("Exercise muscle type is required", ex.getMessage());
    }

    @Test
    void validateRejectsInvalidTrainingValues() {
        WorkoutExercise invalidSets = new WorkoutExercise("Row", MuscleType.BACK, 0, 10, 20.0, null);
        WorkoutExercise invalidReps = new WorkoutExercise("Row", MuscleType.BACK, 4, 0, 20.0, null);
        WorkoutExercise invalidWeight = new WorkoutExercise("Row", MuscleType.BACK, 4, 10, -1.0, null);

        assertEquals("Exercise sets must be at least 1",
                assertThrows(IllegalArgumentException.class, invalidSets::validate).getMessage());
        assertEquals("Exercise reps must be at least 1",
                assertThrows(IllegalArgumentException.class, invalidReps::validate).getMessage());
        assertEquals("Exercise weight (kg) must be 0 or greater",
                assertThrows(IllegalArgumentException.class, invalidWeight::validate).getMessage());
    }
}
