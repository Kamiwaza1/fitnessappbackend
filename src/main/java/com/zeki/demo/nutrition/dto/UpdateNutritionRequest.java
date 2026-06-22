package com.zeki.demo.nutrition.dto;

import com.zeki.demo.nutrition.enums.NutritionCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdateNutritionRequest(
        @Size(min = 2, max = 255, message = "Meal name must be between 2 and 255 characters")
        String mealName,
        LocalDate mealDate,
        @Min(value = 1, message = "Calories must be at least 1")
        @Max(value = 10000, message = "Calories cannot be greater than 10000")
        Integer calories,
        @Min(value = 0, message = "Protein must be at least 0")
        @Max(value = 1000, message = "Protein cannot be greater than 1000")
        Integer proteinGrams,
        @Min(value = 0, message = "Carbs must be at least 0")
        @Max(value = 1000, message = "Carbs cannot be greater than 1000")
        Integer carbsGrams,
        @Min(value = 0, message = "Fat must be at least 0")
        @Max(value = 1000, message = "Fat cannot be greater than 1000")
        Integer fatGrams,
        @Size(max = 1000, message = "Notes cannot be longer than 1000 characters")
        String notes,
        NutritionCategory category
) {
}
