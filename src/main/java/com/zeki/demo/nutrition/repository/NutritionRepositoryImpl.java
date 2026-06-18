package com.zeki.demo.nutrition.repository;

import com.zeki.demo.common.exception.NotFoundException;
import com.zeki.demo.nutrition.domain.Nutrition;
import com.zeki.demo.nutrition.entity.NutritionEntity;
import com.zeki.demo.nutrition.mapperentitydomain.NutritionEntityMapper;
import com.zeki.demo.nutrition.repository.jpa.NutritionRepoJPA;
import com.zeki.demo.users.entity.UserEntity;
import com.zeki.demo.users.repository.jpa.UserRepoJPA;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NutritionRepositoryImpl implements NutritionRepository {

    private final NutritionRepoJPA nutritionRepoJPA;
    private final NutritionEntityMapper nutritionEntityMapper;
    private final UserRepoJPA userRepoJPA;

    @Override
    public Optional<Nutrition> getNutritionByIdAndUserEmail(UUID nutritionId, String userEmail) {
        return nutritionRepoJPA.findByNutritionIdAndUserEmail(nutritionId, userEmail)
                .map(nutritionEntityMapper::toDomain);
    }

    @Override
    public Nutrition save(Nutrition nutrition) {
        UserEntity user = userRepoJPA.findByEmailIgnoreCaseAndIsActiveTrue(nutrition.getUserEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));
        NutritionEntity savedEntity = nutritionRepoJPA.save(nutritionEntityMapper.toEntity(nutrition, user));
        return nutritionEntityMapper.toDomain(savedEntity);
    }

    @Override
    public List<Nutrition> findAllByUserEmail(String userEmail) {
        return nutritionRepoJPA.findAllByUserEmailOrderByMealDateDescNutritionIdDesc(userEmail)
                .stream()
                .map(nutritionEntityMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(UUID nutritionId) {
        NutritionEntity nutritionEntity = nutritionRepoJPA.findById(nutritionId)
                .orElseThrow(() -> new NotFoundException("Nutrition entry not found"));
        nutritionRepoJPA.delete(nutritionEntity);
    }

}
