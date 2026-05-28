package com.zeki.demo.nutrition.service;

import com.zeki.demo.nutrition.domain.Nutrition;
import com.zeki.demo.nutrition.dto.UpdateNutritionRequest;
import java.util.List;
import java.util.UUID;

public interface NutritionService {

    Nutrition createNutrition(Nutrition nutrition);

    Nutrition getNutritionById(UUID nutritionId, String userEmail);

    List<Nutrition> getMyNutritionEntries(String userEmail);

    Nutrition updateNutrition(UUID nutritionId, UpdateNutritionRequest request, String userEmail);

    void deleteNutrition(UUID nutritionId, String userEmail);
}
