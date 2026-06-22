package com.zeki.demo.nutrition.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FoodProductsRequest(
        @Min(value = 1, message = "Limit must be at least 1")
        @Max(value = 100, message = "Limit cannot be greater than 100")
        Integer limit,
        @Min(value = 0, message = "Offset must be at least 0")
        Integer offset,
        @Pattern(regexp = "^[a-zA-Z]{2}$", message = "Language must be a two-letter code")
        String lang,
        @Size(max = 100, message = "Countries cannot be longer than 100 characters")
        String countries,
        Boolean hasNutrition
) {
}
