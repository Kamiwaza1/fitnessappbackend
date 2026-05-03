package com.zeki.demo.nutrition.dto;

import java.util.UUID;

public record DeleteNutritionRequest(
        UUID nutritionId
) {
}
