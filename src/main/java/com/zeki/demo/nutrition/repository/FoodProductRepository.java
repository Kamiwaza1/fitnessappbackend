package com.zeki.demo.nutrition.repository;

import com.zeki.demo.nutrition.dto.FoodProductResponse;
import com.zeki.demo.nutrition.dto.FoodProductSearchRequest;
import com.zeki.demo.nutrition.dto.FoodProductsRequest;
import com.zeki.demo.nutrition.dto.FoodProductsResponse;

public interface FoodProductRepository {

    FoodProductsResponse searchProducts(FoodProductSearchRequest request);

    FoodProductResponse getProductByBarcode(String barcode, String lang);

    FoodProductsResponse getProductsByBrand(String brand, FoodProductsRequest request);

    FoodProductsResponse getProductsByCategory(String category, FoodProductsRequest request);
}
