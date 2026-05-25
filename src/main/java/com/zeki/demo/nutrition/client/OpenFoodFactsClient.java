package com.zeki.demo.nutrition.client;

import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.common.exception.ExternalServiceException;
import com.zeki.demo.common.exception.NotFoundException;
import com.zeki.demo.nutrition.dto.FoodProductResponse;
import com.zeki.demo.nutrition.dto.FoodProductSearchRequest;
import com.zeki.demo.nutrition.dto.FoodProductsRequest;
import com.zeki.demo.nutrition.dto.FoodProductsResponse;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;

@Component
public class OpenFoodFactsClient {

    private final WebClient webClient;

    public OpenFoodFactsClient(
            WebClient.Builder builder,
            @Value("${openfoodfacts.base-url:https://off.brndndiaz.dev}") String baseUrl
    ) {
        this.webClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public FoodProductResponse getProductByBarcode(String barcode, String lang) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> productUri(uriBuilder, barcode, lang))
                    .retrieve()
                    .bodyToMono(FoodProductResponse.class)
                    .block();
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

    private URI productUri(UriBuilder uriBuilder, String barcode, String lang) {
        UriBuilder builder = uriBuilder.path("/product/{barcode}");
        addIfPresent(builder, "lang", lang);
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
}
