package com.zeki.demo.nutrition.repository;

import com.zeki.demo.nutrition.domain.Nutrition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NutritionRepository {

    Optional<Nutrition> getNutritionById(UUID nutritionId);

    Nutrition save(Nutrition nutrition);

    List<Nutrition> findAllByUserEmail(String userEmail);

    void delete(UUID nutritionId);

    boolean isOwnedByUser(UUID nutritionId, String userEmail);
}
