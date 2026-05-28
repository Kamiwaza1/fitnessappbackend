package com.zeki.demo.nutrition.repository.jpa;

import com.zeki.demo.nutrition.entity.NutritionEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NutritionRepoJPA extends JpaRepository<NutritionEntity, UUID> {

    List<NutritionEntity> findAllByUserEmailOrderByMealDateDescNutritionIdDesc(String userEmail);

    Optional<NutritionEntity> findByNutritionIdAndUserEmail(UUID nutritionId, String userEmail);

}
