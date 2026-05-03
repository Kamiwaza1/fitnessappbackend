package com.zeki.demo.nutrition.repository;

import com.zeki.demo.common.exception.NotFoundException;
import com.zeki.demo.nutrition.domain.Nutrition;
import com.zeki.demo.nutrition.entity.NutritionEntity;
import com.zeki.demo.nutrition.mapperEntityDomain.NutritionEntityMapper;
import com.zeki.demo.nutrition.repository.jpa.NutritionRepoJPA;
import com.zeki.demo.users.entity.UserEntity;
import com.zeki.demo.users.repository.JPA.UserRepoJPA;
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
    public Optional<Nutrition> getNutritionById(UUID nutritionId) {
        return nutritionRepoJPA.findById(nutritionId).map(nutritionEntityMapper::toDomain);
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

    @Override
    public boolean isOwnedByUser(UUID nutritionId, String userEmail) {
        return nutritionRepoJPA.existsByNutritionIdAndUserEmail(nutritionId, userEmail);
    }
}
