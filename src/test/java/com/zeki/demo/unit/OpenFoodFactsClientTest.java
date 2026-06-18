package com.zeki.demo.unit;

import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.common.exception.ExternalServiceException;
import com.zeki.demo.common.exception.NotFoundException;
import com.zeki.demo.nutrition.client.OpenFoodFactsClient;
import com.zeki.demo.nutrition.dto.FoodProductResponse;
import com.zeki.demo.nutrition.dto.FoodProductSearchRequest;
import com.zeki.demo.nutrition.dto.FoodProductsRequest;
import com.zeki.demo.nutrition.dto.FoodProductsResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenFoodFactsClientTest {

    private ClientTestServer officialApi;
    private ClientTestServer searchApi;
    private OpenFoodFactsClient client;

    @BeforeEach
    void setUp() {
        officialApi = new ClientTestServer();
        searchApi = new ClientTestServer();
        client = new OpenFoodFactsClient(WebClient.builder(), officialApi.baseUrl(), searchApi.baseUrl(), "DarkLiftTest/1.0");
    }

    @AfterEach
    void tearDown() {
        officialApi.close();
        searchApi.close();
    }

    @Test
    void getProductByBarcodeUsesOfficialApiAndMapsNutritionDetails() {
        officialApi.respondWith(path -> ClientTestServer.Response.json(200, productResponse()));

        FoodProductResponse product = client.getProductByBarcode("12345", "nl");

        assertTrue(officialApi.requests().get(0).startsWith("/product/12345.json?fields="));
        assertTrue(officialApi.requests().get(0).contains("&lc=nl"));
        assertEquals("12345", product.code());
        assertEquals("Protein Bar", product.productName());
        assertEquals(400.0, product.caloriesPer100g());
        assertEquals(200.0, product.caloriesPerServing());
        assertEquals(30.0, product.proteinPer100g());
        assertEquals(40.0, product.carbsPer100g());
        assertEquals(12.0, product.fatPer100g());
        assertEquals(List.of("FitCo", "Protein Lab"), product.brands());
        assertEquals(List.of("Snacks", "Bars"), product.categories());
        assertEquals(List.of("en:netherlands"), product.countriesTags());
    }

    @Test
    void getProductByBarcodeFallsBackToResponseCodeAndEmptyValues() {
        officialApi.respondWith(path -> ClientTestServer.Response.json(200, """
                {"code":"fallback","status":1,"product":{"product_name":"Water","brands":"  ,  "}}
                """));

        FoodProductResponse product = client.getProductByBarcode("fallback", null);

        assertEquals("fallback", product.code());
        assertEquals("Water", product.productName());
        assertTrue(product.brands().isEmpty());
    }

    @Test
    void searchBrandAndCategoryUseOfficialApiQueryParameters() {
        searchApi.respondWith(path -> ClientTestServer.Response.json(200, """
                {"hits": [{"code":"1","product_name":"Rice","brands":["FoodCo","MarketCo"]}], "count": 10}
                """));
        officialApi.respondWith(path -> ClientTestServer.Response.json(200, """
                {"products": [{"code":"1","product_name":"Rice"}], "count": 10}
                """));

        FoodProductsResponse search = client.searchProducts(searchRequest());
        FoodProductsResponse brand = client.getProductsByBrand("fitco", brandProductsRequest());
        FoodProductsResponse category = client.getProductsByCategory("bars", emptyProductsRequest());

        assertEquals(10, search.total());
        assertEquals(List.of("FoodCo", "MarketCo"), search.results().get(0).brands());
        assertEquals(1, brand.count());
        assertEquals("Rice", category.results().get(0).productName());
        assertEquals("/search?q=rice&page_size=10&page=1&lc=en&countries_tags=nl", searchApi.requests().get(0));
        assertEquals("/cgi/search.pl?brands_tags=fitco&json=1&fields=code,product_name,nutriments,serving_size,brands,categories,categories_tags,ingredients_text,nutriscore_grade,nova_group,image_url,image_small_url,countries_tags&page_size=20&page=1&lc=nl&countries_tags=be&nutrition_data=on", officialApi.requests().get(0));
        assertEquals("/cgi/search.pl?categories_tags=bars&json=1&fields=code,product_name,nutriments,serving_size,brands,categories,categories_tags,ingredients_text,nutriscore_grade,nova_group,image_url,image_small_url,countries_tags&page_size=20&page=1&nutrition_data=on", officialApi.requests().get(1));
    }

    @Test
    void missingStatusOrExternalStatusMapsToDomainExceptions() {
        officialApi.respondWith(path -> ClientTestServer.Response.json(200, "{\"status\":0}"));
        assertInstanceOf(NotFoundException.class, assertThrows(RuntimeException.class,
                () -> client.getProductByBarcode("missing", null)));

        searchApi.respondWith(path -> ClientTestServer.Response.json(422, "{}"));
        assertInstanceOf(BadRequestException.class, assertThrows(RuntimeException.class,
                () -> client.searchProducts(minimalSearchRequest())));

        officialApi.respondWith(path -> ClientTestServer.Response.json(503, "{}"));
        assertInstanceOf(ExternalServiceException.class, assertThrows(RuntimeException.class,
                () -> client.getProductsByBrand("x", emptyProductsRequest())));
    }

    private FoodProductsRequest emptyProductsRequest() {
        return new FoodProductsRequest(null, null, null, null, null);
    }

    private FoodProductSearchRequest searchRequest() {
        return new FoodProductSearchRequest("rice", 10, 5, "en", "nl", false);
    }

    private FoodProductSearchRequest minimalSearchRequest() {
        return new FoodProductSearchRequest("x", null, null, null, null, null);
    }

    private FoodProductsRequest brandProductsRequest() {
        return new FoodProductsRequest(20, 0, "nl", "be", true);
    }

    private String productResponse() {
        return """
                {
                  "code": "fallback-code",
                  "status": 1,
                  "product": {
                    "code": "12345",
                    "product_name": "Protein Bar",
                    "nutriments": {
                      "energy-kcal_100g": 400.0,
                      "energy-kcal_serving": 200.0,
                      "proteins_100g": 30.0,
                      "carbohydrates_100g": 40.0,
                      "fat_100g": 12.0,
                      "sugars_100g": 6.0,
                      "salt_100g": 0.8,
                      "fiber_100g": 7.0,
                      "saturated-fat_100g": 3.0,
                      "sodium_100g": 0.32
                    },
                    "serving_size": "50 g",
                    "brands": "FitCo, Protein Lab",
                    "categories": "Snacks, Bars",
                    "ingredients_text": "Oats and whey",
                    "nutriscore_grade": "b",
                    "nova_group": 4.0,
                    "image_url": "large.jpg",
                    "image_small_url": "small.jpg",
                    "countries_tags": ["en:netherlands"]
                  }
                }
                """;
    }
}
