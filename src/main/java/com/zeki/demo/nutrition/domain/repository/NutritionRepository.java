package com.zeki.demo.nutrition.domain.repository;

import com.zeki.demo.nutrition.domain.Nutrition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NutritionRepository {

    Optional<Nutrition> getNutritionByIdAndUserEmail(UUID nutritionId, String userEmail);

    Nutrition save(Nutrition nutrition);

    List<Nutrition> findAllByUserEmail(String userEmail);

    void delete(UUID nutritionId);

}
