ALTER TABLE workouts
    ADD COLUMN IF NOT EXISTS completed BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE workouts
SET completed = FALSE
WHERE completed IS NULL;

UPDATE workouts w
SET completed = TRUE
WHERE EXISTS (
    SELECT 1
    FROM workout_history h
    WHERE h.workout_id = w.workout_id
      AND (
          h.workout_date < w.workout_date
          OR w.workout_date < CURRENT_DATE
      )
);
