package com.zeki.demo;

import com.zeki.demo.nutrition.domain.Nutrition;
import com.zeki.demo.nutrition.dto.CreateNutritionRequest;
import com.zeki.demo.nutrition.dto.NutritionResponse;
import com.zeki.demo.nutrition.enums.NutritionCategory;
import com.zeki.demo.nutrition.mapperDtoDomain.NutritionDtoDomain;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NutritionTest {

    private final NutritionDtoDomain nutritionDtoDomain = new NutritionDtoDomain();

    @Test
    void toDomainUsesProvidedNutritionCategory() {
        CreateNutritionRequest request = new CreateNutritionRequest(
                "Chicken Bowl",
                LocalDate.of(2026, 5, 16),
                650,
                45,
                50,
                20,
                "Post workout meal",
                NutritionCategory.LUNCH,
                UUID.randomUUID()
        );

        Nutrition nutrition = nutritionDtoDomain.toDomain(request, "test@example.com");

        assertEquals(NutritionCategory.LUNCH, nutrition.getCategory());
        assertEquals("test@example.com", nutrition.getUserEmail());
    }

    @Test
    void toDomainDefaultsCategoryToGeneral() {
        CreateNutritionRequest request = new CreateNutritionRequest(
                "Protein Shake",
                LocalDate.of(2026, 5, 17),
                220,
                30,
                12,
                5,
                "Quick snack",
                null,
                UUID.randomUUID()
        );

        Nutrition nutrition = nutritionDtoDomain.toDomain(request, "test@example.com");

        assertEquals(NutritionCategory.GENERAL, nutrition.getCategory());
    }

    @Test
    void responseExposesEnumCategory() {
        Nutrition nutrition = new Nutrition(
                UUID.randomUUID(),
                "Oatmeal",
                LocalDate.of(2026, 5, 16),
                430,
                18,
                55,
                11,
                "Breakfast",
                NutritionCategory.BREAKFAST,
                LocalDate.of(2026, 4, 23),
                LocalDate.of(2026, 4, 23),
                "test@example.com"
        );

        NutritionResponse response = nutritionDtoDomain.toResponse(nutrition);

        assertEquals(NutritionCategory.BREAKFAST, response.category());
    }

    @Test
    void nutritionRejectsNullCategory() {
        Nutrition nutrition = new Nutrition(
                UUID.randomUUID(),
                "Salmon",
                LocalDate.of(2026, 5, 16),
                520,
                40,
                15,
                28,
                "Dinner",
                null,
                LocalDate.of(2026, 4, 23),
                LocalDate.of(2026, 4, 23),
                "test@example.com"
        );

        assertThrows(IllegalArgumentException.class, nutrition::validate);
    }
}
