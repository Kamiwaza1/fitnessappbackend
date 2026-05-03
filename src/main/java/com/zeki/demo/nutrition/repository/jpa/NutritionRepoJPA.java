package com.zeki.demo.nutrition.repository.jpa;

import com.zeki.demo.nutrition.entity.NutritionEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NutritionRepoJPA extends JpaRepository<NutritionEntity, UUID> {

    List<NutritionEntity> findAllByUserEmailOrderByMealDateDescNutritionIdDesc(String userEmail);

    boolean existsByNutritionIdAndUserEmail(UUID nutritionId, String userEmail);
}
