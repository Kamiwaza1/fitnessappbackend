CREATE TABLE IF NOT EXISTS live_workout_rooms (
    room_code VARCHAR(12) PRIMARY KEY,
    host_email VARCHAR(255) NOT NULL,
    workout_id UUID NOT NULL,
    workout_name VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_activity_at TIMESTAMP WITH TIME ZONE NOT NULL,
    closed_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_live_workout_rooms_workout
        FOREIGN KEY (workout_id) REFERENCES workouts(workout_id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_live_workout_rooms_status
    ON live_workout_rooms(status);

CREATE INDEX IF NOT EXISTS idx_live_workout_rooms_host_email
    ON live_workout_rooms(host_email);

CREATE TABLE IF NOT EXISTS live_workout_participants (
    room_code VARCHAR(12) NOT NULL,
    participant_order INTEGER NOT NULL,
    email VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    current_exercise_index INTEGER NOT NULL DEFAULT 0,
    completed_sets INTEGER NOT NULL DEFAULT 0,
    joined_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (room_code, participant_order),
    CONSTRAINT fk_live_workout_participants_room
        FOREIGN KEY (room_code) REFERENCES live_workout_rooms(room_code)
        ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_live_workout_participants_room_email
    ON live_workout_participants(room_code, email);
