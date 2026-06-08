package com.zeki.demo.nutrition.service;

import com.zeki.demo.nutrition.NutritionException.NutritionExistException;
import com.zeki.demo.nutrition.domain.Nutrition;
import com.zeki.demo.nutrition.dto.FoodProductResponse;
import com.zeki.demo.nutrition.dto.FoodProductSearchRequest;
import com.zeki.demo.nutrition.dto.FoodProductsRequest;
import com.zeki.demo.nutrition.dto.FoodProductsResponse;
import com.zeki.demo.nutrition.dto.UpdateNutritionRequest;
import com.zeki.demo.nutrition.mapperDtoDomain.NutritionDtoDomain;
import com.zeki.demo.nutrition.repository.FoodProductRepository;
import com.zeki.demo.nutrition.repository.NutritionRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class NutritionServiceImpl implements NutritionService {

    private final NutritionRepository nutritionRepository;
    private final FoodProductRepository foodProductRepository;
    private final NutritionDtoDomain nutritionDtoDomain;

    @Override
    public Nutrition createNutrition(Nutrition nutrition) {
        nutrition.validate();
        return nutritionRepository.save(nutrition);
    }

    @Override
    public Nutrition getNutritionById(UUID nutritionId, String userEmail) {
        return nutritionRepository.getNutritionByIdAndUserEmail(nutritionId, userEmail)
                .orElseThrow(() -> new NutritionExistException("Nutrition entry not found"));
    }

    @Override
    public List<Nutrition> getMyNutritionEntries(String userEmail) {
        return nutritionRepository.findAllByUserEmail(userEmail);
    }

    @Override
    public FoodProductsResponse searchFoodProducts(FoodProductSearchRequest request) {
        return foodProductRepository.searchProducts(request);
    }

    @Override
    public FoodProductResponse getFoodProductByBarcode(String barcode, String lang) {
        return foodProductRepository.getProductByBarcode(barcode, lang);
    }

    @Override
    public FoodProductsResponse getFoodProductsByBrand(String brand, FoodProductsRequest request) {
        return foodProductRepository.getProductsByBrand(brand, request);
    }

    @Override
    public FoodProductsResponse getFoodProductsByCategory(String category, FoodProductsRequest request) {
        return foodProductRepository.getProductsByCategory(category, request);
    }

    @Override
    public Nutrition updateNutrition(UUID nutritionId, UpdateNutritionRequest request, String userEmail) {
        Nutrition existingNutrition = nutritionRepository.getNutritionByIdAndUserEmail(nutritionId, userEmail)
                .orElseThrow(() -> new NutritionExistException("Nutrition entry not found"));
        Nutrition updatedNutrition = nutritionDtoDomain.toUpdatedDomain(request, existingNutrition);
        updatedNutrition.validate();
        return nutritionRepository.save(updatedNutrition);
    }

    @Override
    public void deleteNutrition(UUID nutritionId, String userEmail) {
        nutritionRepository.getNutritionByIdAndUserEmail(nutritionId, userEmail)
                .orElseThrow(() -> new NutritionExistException("Nutrition entry not found"));
        nutritionRepository.delete(nutritionId);
    }
}
