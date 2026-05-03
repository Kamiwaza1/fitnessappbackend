package com.zeki.demo.nutrition.dto;

import com.zeki.demo.nutrition.enums.NutritionCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

public record CreateNutritionRequest(
        @NotBlank(message = "Meal name is required")
        @Size(min = 2, max = 255, message = "Meal name must be between 2 and 255 characters")
        String mealName,
        @NotNull(message = "Meal date is required")
        LocalDate mealDate,
        @NotNull(message = "Calories is required")
        @Min(value = 1, message = "Calories must be at least 1")
        @Max(value = 10000, message = "Calories cannot be greater than 10000")
        Integer calories,
        @NotNull(message = "Protein is required")
        @Min(value = 0, message = "Protein must be at least 0")
        @Max(value = 1000, message = "Protein cannot be greater than 1000")
        Integer proteinGrams,
        @NotNull(message = "Carbs is required")
        @Min(value = 0, message = "Carbs must be at least 0")
        @Max(value = 1000, message = "Carbs cannot be greater than 1000")
        Integer carbsGrams,
        @NotNull(message = "Fat is required")
        @Min(value = 0, message = "Fat must be at least 0")
        @Max(value = 1000, message = "Fat cannot be greater than 1000")
        Integer fatGrams,
        @Size(max = 1000, message = "Notes cannot be longer than 1000 characters")
        String notes,
        NutritionCategory category,
        UUID nutritionId
) {
}
