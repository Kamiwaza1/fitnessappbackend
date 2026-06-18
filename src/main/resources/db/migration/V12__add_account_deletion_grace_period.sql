ALTER TABLE users
    ADD COLUMN IF NOT EXISTS deletion_requested_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS deletion_scheduled_for TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_users_deletion_scheduled_for
    ON users(deletion_scheduled_for)
    WHERE deletion_scheduled_for IS NOT NULL;
