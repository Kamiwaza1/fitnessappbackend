package com.zeki.demo.nutrition.dto;

import com.zeki.demo.nutrition.enums.NutritionCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record CreateNutritionRequest(
        @NotBlank(message = "Meal name is required")
        String mealName,
        @NotNull(message = "Meal date is required")
        LocalDate mealDate,
        @NotNull(message = "Calories is required")
        Integer calories,
        @NotNull(message = "Protein is required")
        Integer proteinGrams,
        @NotNull(message = "Carbs is required")
        Integer carbsGrams,
        @NotNull(message = "Fat is required")
        Integer fatGrams,
        String notes,
        NutritionCategory category,
        UUID nutritionId
) {
}
