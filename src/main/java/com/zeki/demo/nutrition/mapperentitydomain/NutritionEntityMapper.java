package com.zeki.demo.nutrition.mapperentitydomain;

import com.zeki.demo.nutrition.domain.Nutrition;
import com.zeki.demo.nutrition.entity.NutritionEntity;
import com.zeki.demo.users.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class NutritionEntityMapper {

    public NutritionEntity toEntity(Nutrition nutrition, UserEntity user) {
        return NutritionEntity.builder()
                .nutritionId(nutrition.getNutritionId())
                .mealName(nutrition.getMealName())
                .mealDate(nutrition.getMealDate())
                .calories(nutrition.getCalories())
                .proteinGrams(nutrition.getProteinGrams())
                .carbsGrams(nutrition.getCarbsGrams())
                .fatGrams(nutrition.getFatGrams())
                .notes(nutrition.getNotes())
                .category(nutrition.getCategory())
                .createdAt(nutrition.getCreatedAt())
                .updatedAt(nutrition.getUpdatedAt())
                .userEmail(nutrition.getUserEmail())
                .user(user)
                .build();
    }

    public Nutrition toDomain(NutritionEntity entity) {
        return new Nutrition(
                entity.getNutritionId(),
                entity.getMealName(),
                entity.getMealDate(),
                entity.getCalories(),
                entity.getProteinGrams(),
                entity.getCarbsGrams(),
                entity.getFatGrams(),
                entity.getNotes(),
                entity.getCategory(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getUserEmail()
        );
    }
}
