package com.zeki.demo.nutrition.dto;

import com.zeki.demo.nutrition.enums.NutritionCategory;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateNutritionRequest(
        UUID nutritionId,
        String mealName,
        LocalDate mealDate,
        Integer calories,
        Integer proteinGrams,
        Integer carbsGrams,
        Integer fatGrams,
        String notes,
        NutritionCategory category
) {
}
