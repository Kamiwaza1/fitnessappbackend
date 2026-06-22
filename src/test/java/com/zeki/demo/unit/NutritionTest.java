package com.zeki.demo.unit;

import com.zeki.demo.nutrition.domain.Nutrition;
import com.zeki.demo.nutrition.dto.CreateNutritionRequest;
import com.zeki.demo.nutrition.exception.NutritionValidationException;
import com.zeki.demo.nutrition.dto.NutritionResponse;
import com.zeki.demo.nutrition.enums.NutritionCategory;
import com.zeki.demo.nutrition.mapperdtodomain.NutritionDtoDomain;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NutritionTest {

    private final NutritionDtoDomain nutritionDtoDomain = new NutritionDtoDomain();
    private static final String TEST_EMAIL = "test@example.com";
    private static final LocalDate FIXED_DATE = LocalDate.of(2026, 5, 16);
    private static final LocalDate FIXED_CREATED_AT = LocalDate.of(2026, 4, 23);

    @Test
    void toDomainUsesProvidedNutritionCategory() {
        CreateNutritionRequest request = createNutritionRequest("Chicken Bowl", NutritionCategory.LUNCH);

        Nutrition nutrition = nutritionDtoDomain.toDomain(request, TEST_EMAIL);

        assertEquals(NutritionCategory.LUNCH, nutrition.getCategory());
        assertEquals(TEST_EMAIL, nutrition.getUserEmail());
    }

    @Test
    void toDomainDefaultsCategoryToGeneral() {
        CreateNutritionRequest request = createNutritionRequest("Protein Shake", null);

        Nutrition nutrition = nutritionDtoDomain.toDomain(request, TEST_EMAIL);

        assertEquals(NutritionCategory.GENERAL, nutrition.getCategory());
    }

    @Test
    void responseExposesEnumCategory() {
        Nutrition nutrition = nutrition("Oatmeal", NutritionCategory.BREAKFAST);

        NutritionResponse response = nutritionDtoDomain.toResponse(nutrition);

        assertEquals(NutritionCategory.BREAKFAST, response.category());
    }

    @Test
    void nutritionRejectsNullCategory() {
        Nutrition nutrition = nutrition("Salmon", null);

        assertThrows(NutritionValidationException.class, nutrition::validate);
    }

    private CreateNutritionRequest createNutritionRequest(String mealName, NutritionCategory category) {
        return new CreateNutritionRequest(
                mealName,
                FIXED_DATE,
                650,
                45,
                50,
                20,
                "Post workout meal",
                category
        );
    }

    private Nutrition nutrition(String mealName, NutritionCategory category) {
        return new Nutrition(
                UUID.randomUUID(),
                mealName,
                FIXED_DATE,
                430,
                18,
                55,
                11,
                "Meal note",
                category,
                FIXED_CREATED_AT,
                FIXED_CREATED_AT,
                TEST_EMAIL
        );
    }
}
