package com.zeki.demo.nutrition.exception;

public class NutritionNotFoundException extends RuntimeException {
    public static final String NUTRITION_ENTRY_NOT_FOUND = "Nutrition entry not found";

    public NutritionNotFoundException() {
        super(NUTRITION_ENTRY_NOT_FOUND);
    }

    public NutritionNotFoundException(String message) {
        super(message);
    }
}
