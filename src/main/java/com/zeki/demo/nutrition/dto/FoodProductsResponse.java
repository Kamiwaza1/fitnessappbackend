package com.zeki.demo.nutrition.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FoodProductsResponse(
        List<FoodProductResponse> results,
        Integer count,
        Integer total
) {
}
