ALTER TABLE workouts
    ALTER COLUMN duration_minutes DROP NOT NULL;

ALTER TABLE workout_history
    ALTER COLUMN duration_minutes DROP NOT NULL;

ALTER TABLE workouts
    DROP CONSTRAINT IF EXISTS chk_workouts_training_values_valid;

ALTER TABLE workouts
    ADD CONSTRAINT chk_workouts_training_values_valid
    CHECK (
        (duration_minutes IS NULL OR duration_minutes >= 1)
        AND sets >= 1
        AND reps >= 1
        AND weight_kg >= 0
    );

ALTER TABLE workout_history
    DROP CONSTRAINT IF EXISTS chk_workout_history_training_values_valid;

ALTER TABLE workout_history
    ADD CONSTRAINT chk_workout_history_training_values_valid
    CHECK (
        (duration_minutes IS NULL OR duration_minutes >= 1)
        AND sets >= 1
        AND reps >= 1
        AND weight_kg >= 0
    );
