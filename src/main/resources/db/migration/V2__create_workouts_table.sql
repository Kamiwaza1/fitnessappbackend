CREATE TABLE IF NOT EXISTS workouts (
    workout_id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    category VARCHAR(255) NOT NULL,
    created_at DATE NOT NULL,
    updated_at DATE NOT NULL,
    user_email VARCHAR(255) NOT NULL,
    workout_date DATE NOT NULL,
    duration_minutes INTEGER NOT NULL,
    notes VARCHAR(255),
    user_id UUID NOT NULL,
    CONSTRAINT fk_workouts_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_workouts_user_id ON workouts(user_id);
