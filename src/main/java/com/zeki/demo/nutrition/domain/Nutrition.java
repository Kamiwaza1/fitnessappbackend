package com.zeki.demo.nutrition.domain;

import com.zeki.demo.nutrition.enums.NutritionCategory;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Nutrition {
    private final UUID nutritionId;
    private final String mealName;
    private final LocalDate mealDate;
    private final Integer calories;
    private final Integer proteinGrams;
    private final Integer carbsGrams;
    private final Integer fatGrams;
    private final String notes;
    private final NutritionCategory category;
    private final LocalDate createdAt;
    private final LocalDate updatedAt;
    private final String userEmail;

    public Nutrition(
            UUID nutritionId,
            String mealName,
            LocalDate mealDate,
            Integer calories,
            Integer proteinGrams,
            Integer carbsGrams,
            Integer fatGrams,
            String notes,
            NutritionCategory category,
            LocalDate createdAt,
            LocalDate updatedAt,
            String userEmail
    ) {
        this.nutritionId = nutritionId;
        this.mealName = normalize(mealName);
        this.mealDate = mealDate;
        this.calories = calories;
        this.proteinGrams = proteinGrams;
        this.carbsGrams = carbsGrams;
        this.fatGrams = fatGrams;
        this.notes = normalizeNullable(notes);
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userEmail = normalize(userEmail);
    }

    public void validate() {
        if (mealName == null || mealName.isBlank()) {
            throw new IllegalArgumentException("Meal name is required");
        }
        if (mealName.length() < 2) {
            throw new IllegalArgumentException("Meal name must be at least 2 characters");
        }
        if (mealName.length() > 255) {
            throw new IllegalArgumentException("Meal name cannot be longer than 255 characters");
        }
        if (mealDate == null) {
            throw new IllegalArgumentException("Meal date is required");
        }
        if (mealDate.isBefore(LocalDate.now().minusYears(1))) {
            throw new IllegalArgumentException("Meal date cannot be older than one year");
        }
        if (mealDate.isAfter(LocalDate.now().plusDays(30))) {
            throw new IllegalArgumentException("Meal date cannot be more than 30 days ahead");
        }
        validateMacro(calories, "Calories", 1, 10000);
        validateMacro(proteinGrams, "Protein", 0, 1000);
        validateMacro(carbsGrams, "Carbs", 0, 1000);
        validateMacro(fatGrams, "Fat", 0, 1000);
        if (category == null) {
            throw new IllegalArgumentException("Nutrition category is required");
        }
        if (notes != null && notes.length() > 1000) {
            throw new IllegalArgumentException("Notes cannot be longer than 1000 characters");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Created at is required");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("Updated at is required");
        }
        if (createdAt.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Created at cannot be in the future");
        }
        if (updatedAt.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Updated at cannot be in the future");
        }
        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("Updated at cannot be before created at");
        }
        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException("User email cannot be blank");
        }
    }

    private void validateMacro(Integer value, String label, int min, int max) {
        if (value == null) {
            throw new IllegalArgumentException(label + " is required");
        }
        if (value < min) {
            throw new IllegalArgumentException(label + " must be at least " + min);
        }
        if (value > max) {
            throw new IllegalArgumentException(label + " cannot be greater than " + max);
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeNullable(String value) {
        return value == null ? null : value.trim();
    }
}
