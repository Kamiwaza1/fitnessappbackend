package com.zeki.demo.nutrition.controller;

import com.zeki.demo.nutrition.client.OpenFoodFactsClient;
import com.zeki.demo.nutrition.dto.CreateNutritionRequest;
import com.zeki.demo.nutrition.dto.FoodProductResponse;
import com.zeki.demo.nutrition.dto.FoodProductSearchRequest;
import com.zeki.demo.nutrition.dto.FoodProductsRequest;
import com.zeki.demo.nutrition.dto.FoodProductsResponse;
import com.zeki.demo.nutrition.dto.NutritionResponse;
import com.zeki.demo.nutrition.dto.UpdateNutritionRequest;
import com.zeki.demo.nutrition.mapperDtoDomain.NutritionDtoDomain;
import com.zeki.demo.nutrition.service.NutritionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/nutrition")
@RequiredArgsConstructor
@Tag(name = "Nutrition", description = "Endpoints for managing nutrition entries")
public class NutritionController {

    private final NutritionService nutritionService;
    private final NutritionDtoDomain nutritionDtoDomain;
    private final OpenFoodFactsClient openFoodFactsClient;

    @Operation(summary = "Get all nutrition entries for the current user")
    @ApiResponse(responseCode = "200", description = "List of nutrition entries for the current user")
    @GetMapping
    public List<NutritionResponse> getMyNutritionEntries(Authentication authentication) {
        return nutritionService.getMyNutritionEntries(authentication.getName())
                .stream()
                .map(nutritionDtoDomain::toResponse)
                .toList();
    }

    @Operation(summary = "Search food products from OpenFoodFacts")
    @ApiResponse(responseCode = "200", description = "Food search results")
    @GetMapping("/foods/search")
    public FoodProductsResponse searchFoodProducts(
            @Valid @ModelAttribute FoodProductSearchRequest request,
            Authentication authentication
    ) {
        return openFoodFactsClient.searchProducts(request);
    }

    @Operation(summary = "Get a food product by barcode from OpenFoodFacts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Food product details"),
            @ApiResponse(responseCode = "404", description = "Food product not found")
    })
    @GetMapping("/foods/{barcode}")
    public FoodProductResponse getFoodProductByBarcode(
            @PathVariable String barcode,
            @RequestParam(required = false) String lang,
            Authentication authentication
    ) {
        return openFoodFactsClient.getProductByBarcode(barcode, lang);
    }

    @Operation(summary = "Get food products by brand from OpenFoodFacts")
    @ApiResponse(responseCode = "200", description = "Food products for the requested brand")
    @GetMapping("/foods/brands/{brand}")
    public FoodProductsResponse getFoodProductsByBrand(
            @PathVariable String brand,
            @Valid @ModelAttribute FoodProductsRequest request,
            Authentication authentication
    ) {
        return openFoodFactsClient.getProductsByBrand(brand, request);
    }

    @Operation(summary = "Get food products by category from OpenFoodFacts")
    @ApiResponse(responseCode = "200", description = "Food products for the requested category")
    @GetMapping("/foods/categories/{category}")
    public FoodProductsResponse getFoodProductsByCategory(
            @PathVariable String category,
            @Valid @ModelAttribute FoodProductsRequest request,
            Authentication authentication
    ) {
        return openFoodFactsClient.getProductsByCategory(category, request);
    }

    @Operation(summary = "Get a nutrition entry by ID for the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nutrition entry details for the current user"),
            @ApiResponse(responseCode = "404", description = "Nutrition entry not found")
    })
    @GetMapping("/{nutritionId}")
    public NutritionResponse getNutritionById(@PathVariable UUID nutritionId, Authentication authentication) {
        return nutritionDtoDomain.toResponse(
                nutritionService.getNutritionById(nutritionId, authentication.getName())
        );
    }

    @Operation(summary = "Create a new nutrition entry for the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Nutrition entry created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NutritionResponse createNutrition(
            @Valid @RequestBody CreateNutritionRequest request,
            Authentication authentication
    ) {
        return nutritionDtoDomain.toResponse(
                nutritionService.createNutrition(nutritionDtoDomain.toDomain(request, authentication.getName()))
        );
    }

    @Operation(summary = "Update an existing nutrition entry for the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nutrition entry updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Nutrition entry not found")
    })
    @PatchMapping("/{nutritionId}")
    public NutritionResponse updateNutrition(
            @PathVariable UUID nutritionId,
            @Valid @RequestBody UpdateNutritionRequest request,
            Authentication authentication
    ) {
        return nutritionDtoDomain.toResponse(
                nutritionService.updateNutrition(nutritionId, request, authentication.getName())
        );
    }

    @Operation(summary = "Delete an existing nutrition entry for the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Nutrition entry deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Nutrition entry not found")
    })
    @DeleteMapping("/{nutritionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNutrition(@PathVariable UUID nutritionId, Authentication authentication) {
        nutritionService.deleteNutrition(nutritionId, authentication.getName());
    }
}
