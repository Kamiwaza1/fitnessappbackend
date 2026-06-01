package com.zeki.demo.nutrition.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FoodProductResponse(
        String code,
        @JsonProperty("product_name")
        String productName,
        @JsonProperty("calories_per_100g")
        Double caloriesPer100g,
        @JsonProperty("calories_per_serving")
        Double caloriesPerServing,
        @JsonProperty("protein_per_100g")
        Double proteinPer100g,
        @JsonProperty("carbs_per_100g")
        Double carbsPer100g,
        @JsonProperty("fat_per_100g")
        Double fatPer100g,
        @JsonProperty("sugar_per_100g")
        Double sugarPer100g,
        @JsonProperty("salt_per_100g")
        Double saltPer100g,
        @JsonProperty("fiber_per_100g")
        Double fiberPer100g,
        @JsonProperty("saturated_fat_per_100g")
        Double saturatedFatPer100g,
        @JsonProperty("sodium_per_100g")
        Double sodiumPer100g,
        @JsonProperty("serving_size")
        String servingSize,
        List<String> brands,
        List<String> categories,
        @JsonProperty("ingredients_text")
        String ingredientsText,
        @JsonProperty("nutriscore_grade")
        String nutriscoreGrade,
        @JsonProperty("nova_group")
        Double novaGroup,
        @JsonProperty("image_url")
        String imageUrl,
        @JsonProperty("image_small_url")
        String imageSmallUrl,
        @JsonProperty("countries_tags")
        List<String> countriesTags
) {
}
