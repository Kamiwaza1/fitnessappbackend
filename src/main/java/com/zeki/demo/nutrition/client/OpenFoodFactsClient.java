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

    private static final String PRODUCT_FIELDS = String.join(",", List.of(
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
    ));

    private final WebClient officialWebClient;
    private final WebClient officialSearchWebClient;
    private final WebClient searchWebClient;

    public OpenFoodFactsClient(
            WebClient.Builder builder,
            @Value("${openfoodfacts.official-base-url:https://world.openfoodfacts.org/api/v2}") String officialBaseUrl,
            @Value("${openfoodfacts.search-base-url:https://search.openfoodfacts.org}") String searchBaseUrl,
            @Value("${openfoodfacts.user-agent:DarkLift/1.0 (https://darklift.app)}") String userAgent
    ) {
        this.officialWebClient = builder
                .clone()
                .baseUrl(officialBaseUrl)
                .defaultHeader("User-Agent", userAgent)
                .build();
        this.officialSearchWebClient = builder
                .clone()
                .baseUrl(toOpenFoodFactsRootUrl(officialBaseUrl))
                .defaultHeader("User-Agent", userAgent)
                .build();
        this.searchWebClient = builder
                .clone()
                .baseUrl(searchBaseUrl)
                .defaultHeader("User-Agent", userAgent)
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
            SearchALiciousResponse response = searchWebClient.get()
                    .uri(uriBuilder -> searchALiciousUri(uriBuilder, request))
                    .retrieve()
                    .bodyToMono(SearchALiciousResponse.class)
                    .block();
            return response != null ? response.toFoodProductsResponse() : new FoodProductsResponse(List.of(), 0, 0);
        } catch (WebClientResponseException ex) {
            throw mapExternalError(ex, "Food search failed");
        }
    }

    public FoodProductsResponse getProductsByBrand(String brand, FoodProductsRequest request) {
        try {
            OpenFoodFactsSearchResponse response = officialSearchWebClient.get()
                    .uri(uriBuilder -> officialTaggedProductsUri(uriBuilder, "brands_tags", brand, request))
                    .retrieve()
                    .bodyToMono(OpenFoodFactsSearchResponse.class)
                    .block();
            return response != null ? response.toFoodProductsResponse() : new FoodProductsResponse(List.of(), 0, 0);
        } catch (WebClientResponseException ex) {
            throw mapExternalError(ex, "Brand food products could not be loaded");
        }
    }

    public FoodProductsResponse getProductsByCategory(String category, FoodProductsRequest request) {
        try {
            OpenFoodFactsSearchResponse response = officialSearchWebClient.get()
                    .uri(uriBuilder -> officialTaggedProductsUri(uriBuilder, "categories_tags", category, request))
                    .retrieve()
                    .bodyToMono(OpenFoodFactsSearchResponse.class)
                    .block();
            return response != null ? response.toFoodProductsResponse() : new FoodProductsResponse(List.of(), 0, 0);
        } catch (WebClientResponseException ex) {
            throw mapExternalError(ex, "Category food products could not be loaded");
        }
    }

    private URI officialProductUri(UriBuilder uriBuilder, String barcode, String lang) {
        UriBuilder builder = uriBuilder
                .path("/product/{barcode}.json")
                .queryParam("fields", PRODUCT_FIELDS);
        addIfPresent(builder, "lc", lang);
        return builder.build(barcode);
    }

    private URI searchALiciousUri(UriBuilder uriBuilder, FoodProductSearchRequest request) {
        int pageSize = request.limit() != null ? request.limit() : 20;
        int page = request.offset() != null ? (request.offset() / pageSize) + 1 : 1;
        UriBuilder builder = uriBuilder
                .path("/search")
                .queryParam("q", request.q())
                .queryParam("page_size", pageSize)
                .queryParam("page", page);
        addIfPresent(builder, "lc", request.lang());
        addIfPresent(builder, "countries_tags", request.countries());
        return builder.build();
    }


    private URI officialTaggedProductsUri(UriBuilder uriBuilder, String tagName, String tagValue, FoodProductsRequest request) {
        UriBuilder builder = applyOfficialProductListParams(
                uriBuilder.path("/cgi/search.pl").queryParam(tagName, normalizeTag(tagValue)),
                request.limit(),
                request.offset(),
                request.lang(),
                request.countries(),
                request.hasNutrition()
        );

        return builder.build();
    }

    private UriBuilder applyOfficialProductListParams(
            UriBuilder builder,
            Integer limit,
            Integer offset,
            String lang,
            String countries,
            Boolean hasNutrition
    ) {
        int pageSize = limit != null ? limit : 20;
        int page = offset != null ? (offset / pageSize) + 1 : 1;

        builder.queryParam("json", 1);
        builder.queryParam("fields", PRODUCT_FIELDS);
        builder.queryParam("page_size", pageSize);
        builder.queryParam("page", page);
        addIfPresent(builder, "lc", lang);
        addIfPresent(builder, "countries_tags", countries);
        if (hasNutrition == null || hasNutrition) {
            builder.queryParam("nutrition_data", "on");
        }
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

    private String toOpenFoodFactsRootUrl(String officialBaseUrl) {
        String normalized = officialBaseUrl.endsWith("/") ? officialBaseUrl.substring(0, officialBaseUrl.length() - 1) : officialBaseUrl;
        if (normalized.endsWith("/api/v2")) {
            return normalized.substring(0, normalized.length() - "/api/v2".length());
        }
        return normalized;
    }

    private String normalizeTag(String value) {
        return value == null ? "" : value.trim().toLowerCase().replaceAll("\\s+", "-");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OpenFoodFactsSearchResponse(
            List<OpenFoodFactsProduct> products,
            Integer count
    ) {
        FoodProductsResponse toFoodProductsResponse() {
            List<FoodProductResponse> results = products != null
                    ? products.stream().map(product -> product.toFoodProductResponse(product.code())).toList()
                    : List.of();
            return new FoodProductsResponse(results, results.size(), count != null ? count : results.size());
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record SearchALiciousResponse(
            List<OpenFoodFactsProduct> hits,
            Integer count
    ) {
        FoodProductsResponse toFoodProductsResponse() {
            List<FoodProductResponse> results = hits != null
                    ? hits.stream().map(product -> product.toFoodProductResponse(product.code())).toList()
                    : List.of();
            return new FoodProductsResponse(results, results.size(), count != null ? count : results.size());
        }
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
            Object brands,
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
                    categories != null ? splitCsv(categories) : emptyIfNull(categoriesTags),
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

    private static List<String> splitCsv(Object value) {
        if (value instanceof List<?> items) {
            return items.stream()
                    .map(String::valueOf)
                    .map(String::trim)
                    .filter(item -> !item.isBlank())
                    .toList();
        }
        if (value == null) {
            return List.of();
        }
        String text = value.toString();
        if (text.isBlank()) {
            return List.of();
        }
        return Arrays.stream(text.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList();
    }

    private static List<String> emptyIfNull(List<String> value) {
        return value != null ? value : List.of();
    }
}
