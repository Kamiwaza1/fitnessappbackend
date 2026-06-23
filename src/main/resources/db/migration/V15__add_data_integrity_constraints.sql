DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_users_daily_calorie_target_positive') THEN
        ALTER TABLE users
            ADD CONSTRAINT chk_users_daily_calorie_target_positive
            CHECK (daily_calorie_target IS NULL OR daily_calorie_target > 0);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_users_deletion_schedule_valid') THEN
        ALTER TABLE users
            ADD CONSTRAINT chk_users_deletion_schedule_valid
            CHECK (
                deletion_scheduled_for IS NULL
                OR (
                    deletion_requested_at IS NOT NULL
                    AND deletion_scheduled_for > deletion_requested_at
                )
            );
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_users_role_valid') THEN
        ALTER TABLE users
            ADD CONSTRAINT chk_users_role_valid
            CHECK (role IN ('USER', 'ADMIN', 'TRAINER'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_users_fitness_goal_valid') THEN
        ALTER TABLE users
            ADD CONSTRAINT chk_users_fitness_goal_valid
            CHECK (fitness_goal IN ('GENERAL', 'STRENGTH', 'CARDIO', 'MOBILITY', 'RECOVERY'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_users_experience_level_valid') THEN
        ALTER TABLE users
            ADD CONSTRAINT chk_users_experience_level_valid
            CHECK (experience_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_nutrition_entries_macros_valid') THEN
        ALTER TABLE nutrition_entries
            ADD CONSTRAINT chk_nutrition_entries_macros_valid
            CHECK (
                calories >= 1
                AND protein_grams >= 0
                AND carbs_grams >= 0
                AND fat_grams >= 0
            );
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_nutrition_entries_dates_valid') THEN
        ALTER TABLE nutrition_entries
            ADD CONSTRAINT chk_nutrition_entries_dates_valid
            CHECK (updated_at >= created_at);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_nutrition_entries_category_valid') THEN
        ALTER TABLE nutrition_entries
            ADD CONSTRAINT chk_nutrition_entries_category_valid
            CHECK (category IN ('BREAKFAST', 'LUNCH', 'DINNER', 'SNACK', 'GENERAL'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_workouts_training_values_valid') THEN
        ALTER TABLE workouts
            ADD CONSTRAINT chk_workouts_training_values_valid
            CHECK (
                duration_minutes >= 1
                AND sets >= 1
                AND reps >= 1
                AND weight_kg >= 0
            );
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_workouts_dates_valid') THEN
        ALTER TABLE workouts
            ADD CONSTRAINT chk_workouts_dates_valid
            CHECK (updated_at >= created_at);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_workouts_category_valid') THEN
        ALTER TABLE workouts
            ADD CONSTRAINT chk_workouts_category_valid
            CHECK (category IN ('GENERAL', 'STRENGTH', 'CARDIO', 'MOBILITY', 'RECOVERY'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_workouts_muscle_type_valid') THEN
        ALTER TABLE workouts
            ADD CONSTRAINT chk_workouts_muscle_type_valid
            CHECK (muscle_type IN ('FULL_BODY', 'CHEST', 'BACK', 'SHOULDERS', 'BICEPS', 'TRICEPS', 'LEGS', 'GLUTES', 'CORE', 'CALVES'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_workouts_difficulty_valid') THEN
        ALTER TABLE workouts
            ADD CONSTRAINT chk_workouts_difficulty_valid
            CHECK (difficulty IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_workout_history_training_values_valid') THEN
        ALTER TABLE workout_history
            ADD CONSTRAINT chk_workout_history_training_values_valid
            CHECK (
                duration_minutes >= 1
                AND sets >= 1
                AND reps >= 1
                AND weight_kg >= 0
            );
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_workout_history_dates_valid') THEN
        ALTER TABLE workout_history
            ADD CONSTRAINT chk_workout_history_dates_valid
            CHECK (updated_at >= created_at);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_workout_history_category_valid') THEN
        ALTER TABLE workout_history
            ADD CONSTRAINT chk_workout_history_category_valid
            CHECK (category IN ('GENERAL', 'STRENGTH', 'CARDIO', 'MOBILITY', 'RECOVERY'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_workout_history_muscle_type_valid') THEN
        ALTER TABLE workout_history
            ADD CONSTRAINT chk_workout_history_muscle_type_valid
            CHECK (muscle_type IN ('FULL_BODY', 'CHEST', 'BACK', 'SHOULDERS', 'BICEPS', 'TRICEPS', 'LEGS', 'GLUTES', 'CORE', 'CALVES'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_workout_history_difficulty_valid') THEN
        ALTER TABLE workout_history
            ADD CONSTRAINT chk_workout_history_difficulty_valid
            CHECK (difficulty IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_workout_exercises_order_valid') THEN
        ALTER TABLE workout_exercises
            ADD CONSTRAINT chk_workout_exercises_order_valid
            CHECK (exercise_order >= 0);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_workout_exercises_name_not_blank') THEN
        ALTER TABLE workout_exercises
            ADD CONSTRAINT chk_workout_exercises_name_not_blank
            CHECK (length(btrim(exercise_name)) > 0);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_workout_exercises_training_values_valid') THEN
        ALTER TABLE workout_exercises
            ADD CONSTRAINT chk_workout_exercises_training_values_valid
            CHECK (
                sets >= 1
                AND reps >= 1
                AND weight_kg >= 0
            );
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_workout_exercises_muscle_type_valid') THEN
        ALTER TABLE workout_exercises
            ADD CONSTRAINT chk_workout_exercises_muscle_type_valid
            CHECK (exercise_muscle_type IN ('FULL_BODY', 'CHEST', 'BACK', 'SHOULDERS', 'BICEPS', 'TRICEPS', 'LEGS', 'GLUTES', 'CORE', 'CALVES'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_live_workout_rooms_status_valid') THEN
        ALTER TABLE live_workout_rooms
            ADD CONSTRAINT chk_live_workout_rooms_status_valid
            CHECK (status IN ('ACTIVE', 'CLOSED'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_live_workout_rooms_closed_at_valid') THEN
        ALTER TABLE live_workout_rooms
            ADD CONSTRAINT chk_live_workout_rooms_closed_at_valid
            CHECK (
                (status = 'CLOSED' AND closed_at IS NOT NULL AND closed_at >= created_at)
                OR (status = 'ACTIVE' AND closed_at IS NULL)
            );
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_live_workout_participants_order_valid') THEN
        ALTER TABLE live_workout_participants
            ADD CONSTRAINT chk_live_workout_participants_order_valid
            CHECK (participant_order >= 0);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_live_workout_participants_identity_not_blank') THEN
        ALTER TABLE live_workout_participants
            ADD CONSTRAINT chk_live_workout_participants_identity_not_blank
            CHECK (
                length(btrim(email)) > 0
                AND length(btrim(display_name)) > 0
            );
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_live_workout_participants_progress_valid') THEN
        ALTER TABLE live_workout_participants
            ADD CONSTRAINT chk_live_workout_participants_progress_valid
            CHECK (
                current_exercise_index >= 0
                AND completed_sets >= 0
            );
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_live_workout_participants_status_valid') THEN
        ALTER TABLE live_workout_participants
            ADD CONSTRAINT chk_live_workout_participants_status_valid
            CHECK (status IN ('ACTIVE', 'RESTING'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_live_workout_participants_user_email') THEN
        ALTER TABLE live_workout_participants
            ADD CONSTRAINT fk_live_workout_participants_user_email
            FOREIGN KEY (email) REFERENCES users(email)
            ON UPDATE CASCADE
            ON DELETE CASCADE;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_password_reset_tokens_dates_valid') THEN
        ALTER TABLE password_reset_tokens
            ADD CONSTRAINT chk_password_reset_tokens_dates_valid
            CHECK (
                expires_at > created_at
                AND (used_at IS NULL OR used_at >= created_at)
            ) NOT VALID;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_nutrition_entries_user_id_meal_date
    ON nutrition_entries(user_id, meal_date);

CREATE INDEX IF NOT EXISTS idx_workouts_user_id_workout_date
    ON workouts(user_id, workout_date);

CREATE INDEX IF NOT EXISTS idx_workout_history_user_email_workout_date
    ON workout_history(user_email, workout_date);

CREATE INDEX IF NOT EXISTS idx_live_workout_rooms_active_last_activity
    ON live_workout_rooms(status, last_activity_at)
    WHERE status = 'ACTIVE';

CREATE INDEX IF NOT EXISTS idx_live_workout_participants_email
    ON live_workout_participants(email);
