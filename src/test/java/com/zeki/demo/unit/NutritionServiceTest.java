package com.zeki.demo.unit;

import com.zeki.demo.nutrition.nutritionexception.NutritionExistException;
import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.nutrition.domain.Nutrition;
import com.zeki.demo.nutrition.dto.UpdateNutritionRequest;
import com.zeki.demo.nutrition.enums.NutritionCategory;
import com.zeki.demo.nutrition.mapperdtodomain.NutritionDtoDomain;
import com.zeki.demo.nutrition.repository.FoodProductRepository;
import com.zeki.demo.nutrition.repository.NutritionRepository;
import com.zeki.demo.nutrition.service.NutritionServiceImpl;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NutritionServiceTest {

    @Mock
    private NutritionRepository nutritionRepository;

    @Mock
    private FoodProductRepository foodProductRepository;

    private NutritionServiceImpl nutritionService;

    @BeforeEach
    void setUp() {
        nutritionService = new NutritionServiceImpl(nutritionRepository, foodProductRepository, new NutritionDtoDomain());
    }

    @Test
    void getNutritionByIdWhenMissingThrowsNotFound() {
        UUID nutritionId = UUID.randomUUID();
        when(nutritionRepository.getNutritionByIdAndUserEmail(nutritionId, "owner@example.com"))
                .thenReturn(Optional.empty());

        NutritionExistException ex = assertThrows(
                NutritionExistException.class,
                () -> nutritionService.getNutritionById(nutritionId, "owner@example.com")
        );

        assertEquals("Nutrition entry not found", ex.getMessage());
    }

    @Test
    void getMyNutritionEntriesDelegatesToRepository() {
        List<Nutrition> entries = List.of(validNutrition(UUID.randomUUID(), "owner@example.com"));
        when(nutritionRepository.findAllByUserEmail("owner@example.com")).thenReturn(entries);

        List<Nutrition> result = nutritionService.getMyNutritionEntries("owner@example.com");

        assertEquals(entries, result);
    }

    @Test
    void updateNutritionWhenNotOwnedThrowsNotFound() {
        UUID nutritionId = UUID.randomUUID();
        UpdateNutritionRequest request = updateRequest(nutritionId);

        when(nutritionRepository.getNutritionByIdAndUserEmail(nutritionId, "other@example.com"))
                .thenReturn(Optional.empty());

        NutritionExistException ex = assertThrows(
                NutritionExistException.class,
                () -> nutritionService.updateNutrition(nutritionId, request, "other@example.com", null)
        );

        assertEquals("Nutrition entry not found", ex.getMessage());
        verify(nutritionRepository, never()).save(any());
    }

    @Test
    void updateNutritionWhenOwnedSavesUpdatedNutrition() {
        UUID nutritionId = UUID.randomUUID();
        Nutrition existing = validNutrition(nutritionId, "owner@example.com");
        UpdateNutritionRequest request = updateRequest(nutritionId);

        when(nutritionRepository.getNutritionByIdAndUserEmail(nutritionId, "owner@example.com"))
                .thenReturn(Optional.of(existing));
        when(nutritionRepository.save(any(Nutrition.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Nutrition updated = nutritionService.updateNutrition(nutritionId, request, "owner@example.com", null);

        assertEquals("Updated meal", updated.getMealName());
        assertEquals(700, updated.getCalories());
        assertEquals(NutritionCategory.DINNER, updated.getCategory());
    }

    @Test
    void createNutritionRejectsPastMealDate() {
        Nutrition pastNutrition = nutritionWithDate(UUID.randomUUID(), "owner@example.com", LocalDate.now().minusDays(1));

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> nutritionService.createNutrition(pastNutrition, null)
        );

        assertEquals("Food cannot be added to a past day", ex.getMessage());
        verify(nutritionRepository, never()).save(any());
    }

    @Test
    void updateNutritionRejectsPastExistingEntry() {
        UUID nutritionId = UUID.randomUUID();
        Nutrition existing = nutritionWithDate(nutritionId, "owner@example.com", LocalDate.now().minusDays(1));
        when(nutritionRepository.getNutritionByIdAndUserEmail(nutritionId, "owner@example.com"))
                .thenReturn(Optional.of(existing));

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> nutritionService.updateNutrition(nutritionId, updateRequest(nutritionId), "owner@example.com", null)
        );

        assertEquals("Past nutrition entries cannot be changed", ex.getMessage());
        verify(nutritionRepository, never()).save(any());
    }

    @Test
    void updateNutritionRejectsMovingEntryToPastDate() {
        UUID nutritionId = UUID.randomUUID();
        Nutrition existing = validNutrition(nutritionId, "owner@example.com");
        UpdateNutritionRequest request = new UpdateNutritionRequest(
                nutritionId,
                "Updated meal",
                LocalDate.now().minusDays(1),
                700,
                50,
                55,
                15,
                "updated notes",
                NutritionCategory.DINNER
        );
        when(nutritionRepository.getNutritionByIdAndUserEmail(nutritionId, "owner@example.com"))
                .thenReturn(Optional.of(existing));

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> nutritionService.updateNutrition(nutritionId, request, "owner@example.com", null)
        );

        assertEquals("Food cannot be added to a past day", ex.getMessage());
        verify(nutritionRepository, never()).save(any());
    }

    @Test
    void deleteNutritionWhenFoundDeletesById() {
        UUID nutritionId = UUID.randomUUID();
        when(nutritionRepository.getNutritionByIdAndUserEmail(nutritionId, "owner@example.com"))
                .thenReturn(Optional.of(validNutrition(nutritionId, "owner@example.com")));

        nutritionService.deleteNutrition(nutritionId, "owner@example.com", null);

        verify(nutritionRepository).delete(nutritionId);
    }

    @Test
    void deleteNutritionRejectsPastExistingEntry() {
        UUID nutritionId = UUID.randomUUID();
        when(nutritionRepository.getNutritionByIdAndUserEmail(nutritionId, "owner@example.com"))
                .thenReturn(Optional.of(nutritionWithDate(nutritionId, "owner@example.com", LocalDate.now().minusDays(1))));

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> nutritionService.deleteNutrition(nutritionId, "owner@example.com", null)
        );

        assertEquals("Past nutrition entries cannot be changed", ex.getMessage());
        verify(nutritionRepository, never()).delete(nutritionId);
    }

    private Nutrition validNutrition(UUID id, String userEmail) {
        return nutritionWithDate(id, userEmail, LocalDate.now());
    }

    private Nutrition nutritionWithDate(UUID id, String userEmail, LocalDate mealDate) {
        LocalDate now = LocalDate.now();
        return new Nutrition(
                id,
                "Chicken Bowl",
                mealDate,
                650,
                45,
                50,
                20,
                "notes",
                NutritionCategory.LUNCH,
                now.minusDays(1),
                now,
                userEmail
        );
    }

    private UpdateNutritionRequest updateRequest(UUID id) {
        return new UpdateNutritionRequest(
                id,
                "Updated meal",
                LocalDate.now(),
                700,
                50,
                55,
                15,
                "updated notes",
                NutritionCategory.DINNER
        );
    }
}
