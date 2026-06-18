ALTER TABLE users
    ADD COLUMN IF NOT EXISTS fitness_goal VARCHAR(255) NOT NULL DEFAULT 'GENERAL',
    ADD COLUMN IF NOT EXISTS experience_level VARCHAR(255) NOT NULL DEFAULT 'BEGINNER';

ALTER TABLE workouts
    ADD COLUMN IF NOT EXISTS difficulty VARCHAR(255) NOT NULL DEFAULT 'BEGINNER';

ALTER TABLE workout_history
    ADD COLUMN IF NOT EXISTS difficulty VARCHAR(255) NOT NULL DEFAULT 'BEGINNER';

UPDATE users
SET fitness_goal = 'GENERAL'
WHERE fitness_goal IS NULL;

UPDATE users
SET experience_level = 'BEGINNER'
WHERE experience_level IS NULL;

UPDATE workouts
SET difficulty = 'BEGINNER'
WHERE difficulty IS NULL;

UPDATE workout_history
SET difficulty = 'BEGINNER'
WHERE difficulty IS NULL;

CREATE TABLE IF NOT EXISTS recommendation_feedback (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    workout_id UUID NOT NULL,
    ignored BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_recommendation_feedback_user_workout UNIQUE (user_id, workout_id),
    CONSTRAINT fk_recommendation_feedback_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_recommendation_feedback_workout FOREIGN KEY (workout_id) REFERENCES workouts(workout_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_recommendation_feedback_user_id
    ON recommendation_feedback(user_id);

CREATE INDEX IF NOT EXISTS idx_recommendation_feedback_workout_id
    ON recommendation_feedback(workout_id);

CREATE OR REPLACE FUNCTION sync_workout_history_from_workouts()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF TG_OP = 'INSERT' OR TG_OP = 'UPDATE' THEN
        INSERT INTO workout_history (
            workout_id,
            name,
            description,
            category,
            muscle_type,
            difficulty,
            created_at,
            updated_at,
            user_email,
            workout_date,
            duration_minutes,
            sets,
            reps,
            weight_kg,
            notes
        )
        VALUES (
            NEW.workout_id,
            NEW.name,
            NEW.description,
            NEW.category,
            COALESCE(NEW.muscle_type, 'FULL_BODY'),
            COALESCE(NEW.difficulty, 'BEGINNER'),
            NEW.created_at,
            NEW.updated_at,
            NEW.user_email,
            NEW.workout_date,
            NEW.duration_minutes,
            NEW.sets,
            NEW.reps,
            NEW.weight_kg,
            NEW.notes
        )
        ON CONFLICT (workout_id) DO UPDATE SET
            name = EXCLUDED.name,
            description = EXCLUDED.description,
            category = EXCLUDED.category,
            muscle_type = EXCLUDED.muscle_type,
            difficulty = EXCLUDED.difficulty,
            created_at = EXCLUDED.created_at,
            updated_at = EXCLUDED.updated_at,
            user_email = EXCLUDED.user_email,
            workout_date = EXCLUDED.workout_date,
            duration_minutes = EXCLUDED.duration_minutes,
            sets = EXCLUDED.sets,
            reps = EXCLUDED.reps,
            weight_kg = EXCLUDED.weight_kg,
            notes = EXCLUDED.notes;
        RETURN NEW;
    END IF;

    IF TG_OP = 'DELETE' THEN
        INSERT INTO workout_history (
            workout_id,
            name,
            description,
            category,
            muscle_type,
            difficulty,
            created_at,
            updated_at,
            user_email,
            workout_date,
            duration_minutes,
            sets,
            reps,
            weight_kg,
            notes
        )
        VALUES (
            OLD.workout_id,
            OLD.name,
            OLD.description,
            OLD.category,
            COALESCE(OLD.muscle_type, 'FULL_BODY'),
            COALESCE(OLD.difficulty, 'BEGINNER'),
            OLD.created_at,
            OLD.updated_at,
            OLD.user_email,
            OLD.workout_date,
            OLD.duration_minutes,
            OLD.sets,
            OLD.reps,
            OLD.weight_kg,
            OLD.notes
        )
        ON CONFLICT (workout_id) DO UPDATE SET
            name = EXCLUDED.name,
            description = EXCLUDED.description,
            category = EXCLUDED.category,
            muscle_type = EXCLUDED.muscle_type,
            difficulty = EXCLUDED.difficulty,
            created_at = EXCLUDED.created_at,
            updated_at = EXCLUDED.updated_at,
            user_email = EXCLUDED.user_email,
            workout_date = EXCLUDED.workout_date,
            duration_minutes = EXCLUDED.duration_minutes,
            sets = EXCLUDED.sets,
            reps = EXCLUDED.reps,
            weight_kg = EXCLUDED.weight_kg,
            notes = EXCLUDED.notes;
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$;
