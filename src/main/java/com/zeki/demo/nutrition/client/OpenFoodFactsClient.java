package com.zeki.demo.nutrition.client;

import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.common.exception.ExternalServiceException;
import com.zeki.demo.common.exception.NotFoundException;
import com.zeki.demo.nutrition.dto.FoodProductResponse;
import com.zeki.demo.nutrition.dto.FoodProductSearchRequest;
import com.zeki.demo.nutrition.dto.FoodProductsRequest;
import com.zeki.demo.nutrition.dto.FoodProductsResponse;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;

@Component
public class OpenFoodFactsClient {

    private final WebClient webClient;
    private final WebClient officialWebClient;

    public OpenFoodFactsClient(
            WebClient.Builder builder,
            @Value("${openfoodfacts.base-url:https://off.brndndiaz.dev}") String baseUrl,
            @Value("${openfoodfacts.official-base-url:https://world.openfoodfacts.org/api/v2}") String officialBaseUrl
    ) {
        this.webClient = builder
                .baseUrl(baseUrl)
                .build();
        this.officialWebClient = builder
                .clone()
                .baseUrl(officialBaseUrl)
                .build();
    }

    public FoodProductResponse getProductByBarcode(String barcode, String lang) {
        try {
            OpenFoodFactsProductResponse response = officialWebClient.get()
                    .uri(uriBuilder -> officialProductUri(uriBuilder, barcode, lang))
                    .retrieve()
                    .bodyToMono(OpenFoodFactsProductResponse.class)
                    .block();
            if (response == null || response.status() == null || response.status() == 0 || response.product() == null) {
                throw new NotFoundException("Food product not found");
            }
            return response.product().toFoodProductResponse(response.code());
        } catch (WebClientResponseException ex) {
            throw mapExternalError(ex, "Food product not found");
        }
    }

    public FoodProductsResponse searchProducts(FoodProductSearchRequest request) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> searchUri(uriBuilder, request))
                    .retrieve()
                    .bodyToMono(FoodProductsResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            throw mapExternalError(ex, "Food search failed");
        }
    }

    public FoodProductsResponse getProductsByBrand(String brand, FoodProductsRequest request) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> productsUri(uriBuilder, "/brands/{brand}", brand, request))
                    .retrieve()
                    .bodyToMono(FoodProductsResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            throw mapExternalError(ex, "Brand food products could not be loaded");
        }
    }

    public FoodProductsResponse getProductsByCategory(String category, FoodProductsRequest request) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> productsUri(uriBuilder, "/categories/{category}", category, request))
                    .retrieve()
                    .bodyToMono(FoodProductsResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            throw mapExternalError(ex, "Category food products could not be loaded");
        }
    }

    private URI officialProductUri(UriBuilder uriBuilder, String barcode, String lang) {
        UriBuilder builder = uriBuilder
                .path("/product/{barcode}.json")
                .queryParam("fields", String.join(",", List.of(
                        "code",
                        "product_name",
                        "nutriments",
                        "serving_size",
                        "brands",
                        "categories",
                        "categories_tags",
                        "ingredients_text",
                        "nutriscore_grade",
                        "nova_group",
                        "image_url",
                        "image_small_url",
                        "countries_tags"
                )));
        addIfPresent(builder, "lc", lang);
        return builder.build(barcode);
    }

    private URI searchUri(UriBuilder uriBuilder, FoodProductSearchRequest request) {
        UriBuilder builder = applyProductListParams(
                uriBuilder.path("/search").queryParam("q", request.q()),
                request.limit(),
                request.offset(),
                request.lang(),
                request.countries(),
                request.hasNutrition()
        );

        return builder.build();
    }

    private URI productsUri(UriBuilder uriBuilder, String path, String value, FoodProductsRequest request) {
        UriBuilder builder = applyProductListParams(
                uriBuilder.path(path),
                request.limit(),
                request.offset(),
                request.lang(),
                request.countries(),
                request.hasNutrition()
        );

        return builder.build(value);
    }

    private UriBuilder applyProductListParams(
            UriBuilder builder,
            Integer limit,
            Integer offset,
            String lang,
            String countries,
            Boolean hasNutrition
    ) {
        addIfPresent(builder, "limit", limit);
        addIfPresent(builder, "offset", offset);
        addIfPresent(builder, "lang", lang);
        addIfPresent(builder, "countries", countries);
        addIfPresent(builder, "has_nutrition", hasNutrition != null ? hasNutrition : true);
        return builder;
    }

    private void addIfPresent(UriBuilder builder, String name, Object value) {
        if (value != null) {
            builder.queryParam(name, value);
        }
    }

    private RuntimeException mapExternalError(WebClientResponseException ex, String defaultMessage) {
        if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            return new NotFoundException(defaultMessage);
        }
        if (ex.getStatusCode().is4xxClientError()) {
            return new BadRequestException(defaultMessage);
        }
        return new ExternalServiceException("OpenFoodFacts service is unavailable");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OpenFoodFactsProductResponse(
            String code,
            Integer status,
            OpenFoodFactsProduct product
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OpenFoodFactsProduct(
            String code,
            @JsonProperty("product_name")
            String productName,
            Nutriments nutriments,
            @JsonProperty("serving_size")
            String servingSize,
            String brands,
            String categories,
            @JsonProperty("categories_tags")
            List<String> categoriesTags,
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
        FoodProductResponse toFoodProductResponse(String fallbackCode) {
            Nutriments values = nutriments != null ? nutriments : Nutriments.empty();
            return new FoodProductResponse(
                    code != null ? code : fallbackCode,
                    productName,
                    values.caloriesPer100g(),
                    values.caloriesPerServing(),
                    values.proteins100g(),
                    values.carbohydrates100g(),
                    values.fat100g(),
                    values.sugars100g(),
                    values.salt100g(),
                    values.fiber100g(),
                    values.saturatedFat100g(),
                    values.sodium100g(),
                    servingSize,
                    splitCsv(brands),
                    categories != null ? splitCsv(categories) : categoriesTags,
                    ingredientsText,
                    nutriscoreGrade,
                    novaGroup,
                    imageUrl,
                    imageSmallUrl,
                    countriesTags
            );
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Nutriments(
            @JsonProperty("energy-kcal_100g")
            Double caloriesPer100g,
            @JsonProperty("energy-kcal_serving")
            Double caloriesPerServing,
            @JsonProperty("proteins_100g")
            Double proteins100g,
            @JsonProperty("carbohydrates_100g")
            Double carbohydrates100g,
            @JsonProperty("fat_100g")
            Double fat100g,
            @JsonProperty("sugars_100g")
            Double sugars100g,
            @JsonProperty("salt_100g")
            Double salt100g,
            @JsonProperty("fiber_100g")
            Double fiber100g,
            @JsonProperty("saturated-fat_100g")
            Double saturatedFat100g,
            @JsonProperty("sodium_100g")
            Double sodium100g
    ) {
        static Nutriments empty() {
            return new Nutriments(null, null, null, null, null, null, null, null, null, null);
        }
    }

    private static List<String> splitCsv(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList();
    }
}
