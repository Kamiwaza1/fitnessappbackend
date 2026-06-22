package com.zeki.demo.nutrition.mapperdtodomain;

import com.zeki.demo.nutrition.domain.Nutrition;
import com.zeki.demo.nutrition.dto.CreateNutritionRequest;
import com.zeki.demo.nutrition.dto.NutritionResponse;
import com.zeki.demo.nutrition.dto.UpdateNutritionRequest;
import com.zeki.demo.nutrition.enums.NutritionCategory;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class NutritionDtoDomain {

    private static final NutritionCategory DEFAULT_CATEGORY = NutritionCategory.GENERAL;

    public Nutrition toDomain(CreateNutritionRequest request, String userEmail) {
        LocalDate now = LocalDate.now();
        return new Nutrition(
                null,
                request.mealName(),
                request.mealDate(),
                request.calories(),
                request.proteinGrams(),
                request.carbsGrams(),
                request.fatGrams(),
                request.notes(),
                request.category() != null ? request.category() : DEFAULT_CATEGORY,
                now,
                now,
                userEmail
        );
    }

    public Nutrition toUpdatedDomain(UpdateNutritionRequest request, Nutrition existingNutrition) {
        return new Nutrition(
                existingNutrition.getNutritionId(),
                request.mealName() != null ? request.mealName() : existingNutrition.getMealName(),
                request.mealDate() != null ? request.mealDate() : existingNutrition.getMealDate(),
                request.calories() != null ? request.calories() : existingNutrition.getCalories(),
                request.proteinGrams() != null ? request.proteinGrams() : existingNutrition.getProteinGrams(),
                request.carbsGrams() != null ? request.carbsGrams() : existingNutrition.getCarbsGrams(),
                request.fatGrams() != null ? request.fatGrams() : existingNutrition.getFatGrams(),
                request.notes() != null ? request.notes() : existingNutrition.getNotes(),
                request.category() != null ? request.category() : existingNutrition.getCategory(),
                existingNutrition.getCreatedAt(),
                LocalDate.now(),
                existingNutrition.getUserEmail()
        );
    }

    public NutritionResponse toResponse(Nutrition nutrition) {
        return new NutritionResponse(
                nutrition.getNutritionId(),
                nutrition.getMealName(),
                nutrition.getMealDate(),
                nutrition.getCalories(),
                nutrition.getProteinGrams(),
                nutrition.getCarbsGrams(),
                nutrition.getFatGrams(),
                nutrition.getNotes(),
                nutrition.getCategory(),
                nutrition.getUpdatedAt(),
                nutrition.getCreatedAt()
        );
    }
}
