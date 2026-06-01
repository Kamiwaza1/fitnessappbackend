ALTER TABLE users
    ADD COLUMN IF NOT EXISTS daily_calorie_target INTEGER;
