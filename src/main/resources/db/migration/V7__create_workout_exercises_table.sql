CREATE TABLE IF NOT EXISTS workout_exercises (
    workout_id UUID NOT NULL,
    exercise_order INTEGER NOT NULL,
    exercise_name VARCHAR(255) NOT NULL,
    exercise_muscle_type VARCHAR(255) NOT NULL DEFAULT 'FULL_BODY',
    sets INTEGER NOT NULL,
    reps INTEGER NOT NULL,
    weight_kg DOUBLE PRECISION NOT NULL DEFAULT 0,
    notes VARCHAR(1000),
    PRIMARY KEY (workout_id, exercise_order),
    CONSTRAINT fk_workout_exercises_workout
        FOREIGN KEY (workout_id) REFERENCES workouts(workout_id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_workout_exercises_workout_id
    ON workout_exercises(workout_id);
