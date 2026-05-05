CREATE TABLE IF NOT EXISTS nutrition_entries (
    nutrition_id UUID PRIMARY KEY,
    meal_name VARCHAR(255) NOT NULL,
    meal_date DATE NOT NULL,
    calories INTEGER NOT NULL,
    protein_grams INTEGER NOT NULL,
    carbs_grams INTEGER NOT NULL,
    fat_grams INTEGER NOT NULL,
    notes VARCHAR(255),
    category VARCHAR(255) NOT NULL,
    created_at DATE NOT NULL,
    updated_at DATE NOT NULL,
    user_email VARCHAR(255) NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT fk_nutrition_entries_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_nutrition_entries_user_id ON nutrition_entries(user_id);
