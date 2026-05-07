CREATE TABLE IF NOT EXISTS workout_history (
    workout_id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    category VARCHAR(255) NOT NULL,
    muscle_type VARCHAR(255) NOT NULL DEFAULT 'FULL_BODY',
    created_at DATE NOT NULL,
    updated_at DATE NOT NULL,
    user_email VARCHAR(255) NOT NULL,
    workout_date DATE NOT NULL,
    duration_minutes INTEGER NOT NULL,
    sets INTEGER NOT NULL DEFAULT 1,
    reps INTEGER NOT NULL DEFAULT 1,
    weight_kg DOUBLE PRECISION NOT NULL DEFAULT 0,
    notes VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_workout_history_user_email
    ON workout_history(user_email);

CREATE INDEX IF NOT EXISTS idx_workout_history_workout_date
    ON workout_history(workout_date);

INSERT INTO workout_history (
    workout_id,
    name,
    description,
    category,
    muscle_type,
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
SELECT
    w.workout_id,
    w.name,
    w.description,
    w.category,
    COALESCE(w.muscle_type, 'FULL_BODY'),
    w.created_at,
    w.updated_at,
    w.user_email,
    w.workout_date,
    w.duration_minutes,
    w.sets,
    w.reps,
    w.weight_kg,
    w.notes
FROM workouts w
ON CONFLICT (workout_id) DO NOTHING;

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

DROP TRIGGER IF EXISTS trg_sync_workout_history_from_workouts ON workouts;

CREATE TRIGGER trg_sync_workout_history_from_workouts
AFTER INSERT OR UPDATE OR DELETE ON workouts
FOR EACH ROW
EXECUTE FUNCTION sync_workout_history_from_workouts();
