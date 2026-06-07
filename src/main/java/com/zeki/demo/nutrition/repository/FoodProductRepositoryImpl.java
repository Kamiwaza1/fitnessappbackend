package com.zeki.demo.nutrition.repository;

import com.zeki.demo.nutrition.client.OpenFoodFactsClient;
import com.zeki.demo.nutrition.dto.FoodProductResponse;
import com.zeki.demo.nutrition.dto.FoodProductSearchRequest;
import com.zeki.demo.nutrition.dto.FoodProductsRequest;
import com.zeki.demo.nutrition.dto.FoodProductsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FoodProductRepositoryImpl implements FoodProductRepository {

    private final OpenFoodFactsClient openFoodFactsClient;

    @Override
    public FoodProductsResponse searchProducts(FoodProductSearchRequest request) {
        return openFoodFactsClient.searchProducts(request);
    }

    @Override
    public FoodProductResponse getProductByBarcode(String barcode, String lang) {
        return openFoodFactsClient.getProductByBarcode(barcode, lang);
    }

    @Override
    public FoodProductsResponse getProductsByBrand(String brand, FoodProductsRequest request) {
        return openFoodFactsClient.getProductsByBrand(brand, request);
    }

    @Override
    public FoodProductsResponse getProductsByCategory(String category, FoodProductsRequest request) {
        return openFoodFactsClient.getProductsByCategory(category, request);
    }
}
