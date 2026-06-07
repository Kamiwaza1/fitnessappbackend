package com.zeki.demo.nutrition.service;

import com.zeki.demo.nutrition.domain.Nutrition;
import com.zeki.demo.nutrition.dto.FoodProductResponse;
import com.zeki.demo.nutrition.dto.FoodProductSearchRequest;
import com.zeki.demo.nutrition.dto.FoodProductsRequest;
import com.zeki.demo.nutrition.dto.FoodProductsResponse;
import com.zeki.demo.nutrition.dto.UpdateNutritionRequest;
import java.util.List;
import java.util.UUID;

public interface NutritionService {

    Nutrition createNutrition(Nutrition nutrition);

    Nutrition getNutritionById(UUID nutritionId, String userEmail);

    List<Nutrition> getMyNutritionEntries(String userEmail);

    FoodProductsResponse searchFoodProducts(FoodProductSearchRequest request);

    FoodProductResponse getFoodProductByBarcode(String barcode, String lang);

    FoodProductsResponse getFoodProductsByBrand(String brand, FoodProductsRequest request);

    FoodProductsResponse getFoodProductsByCategory(String category, FoodProductsRequest request);

    Nutrition updateNutrition(UUID nutritionId, UpdateNutritionRequest request, String userEmail);

    void deleteNutrition(UUID nutritionId, String userEmail);
}
