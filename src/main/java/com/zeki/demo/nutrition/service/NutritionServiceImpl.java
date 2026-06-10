package com.zeki.demo.nutrition.service;

import com.zeki.demo.common.exception.BadRequestException;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.DateTimeException;
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
    public Nutrition createNutrition(Nutrition nutrition, String timeZone) {
        ensureCurrentOrFutureMealDate(nutrition.getMealDate(), timeZone);
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
    public Nutrition updateNutrition(UUID nutritionId, UpdateNutritionRequest request, String userEmail, String timeZone) {
        Nutrition existingNutrition = nutritionRepository.getNutritionByIdAndUserEmail(nutritionId, userEmail)
                .orElseThrow(() -> new NutritionExistException("Nutrition entry not found"));
        ensureEntryCanStillBeChanged(existingNutrition, timeZone);
        Nutrition updatedNutrition = nutritionDtoDomain.toUpdatedDomain(request, existingNutrition);
        ensureCurrentOrFutureMealDate(updatedNutrition.getMealDate(), timeZone);
        updatedNutrition.validate();
        return nutritionRepository.save(updatedNutrition);
    }

    @Override
    public void deleteNutrition(UUID nutritionId, String userEmail, String timeZone) {
        Nutrition existingNutrition = nutritionRepository.getNutritionByIdAndUserEmail(nutritionId, userEmail)
                .orElseThrow(() -> new NutritionExistException("Nutrition entry not found"));
        ensureEntryCanStillBeChanged(existingNutrition, timeZone);
        nutritionRepository.delete(nutritionId);
    }

    private void ensureEntryCanStillBeChanged(Nutrition nutrition, String timeZone) {
        if (nutrition.getMealDate() != null && nutrition.getMealDate().isBefore(today(timeZone))) {
            throw new BadRequestException("Past nutrition entries cannot be changed");
        }
    }

    private void ensureCurrentOrFutureMealDate(LocalDate mealDate, String timeZone) {
        if (mealDate != null && mealDate.isBefore(today(timeZone))) {
            throw new BadRequestException("Food cannot be added to a past day");
        }
    }

    private LocalDate today(String timeZone) {
        return LocalDate.now(resolveZone(timeZone));
    }

    private ZoneId resolveZone(String timeZone) {
        if (timeZone == null || timeZone.isBlank()) {
            return ZoneId.systemDefault();
        }
        try {
            return ZoneId.of(timeZone.trim());
        } catch (DateTimeException ex) {
            return ZoneId.systemDefault();
        }
    }
}
