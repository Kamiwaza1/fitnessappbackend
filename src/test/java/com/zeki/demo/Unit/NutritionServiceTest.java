package com.zeki.demo.Unit;

import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.nutrition.NutritionException.NutritionExistException;
import com.zeki.demo.nutrition.domain.Nutrition;
import com.zeki.demo.nutrition.dto.UpdateNutritionRequest;
import com.zeki.demo.nutrition.enums.NutritionCategory;
import com.zeki.demo.nutrition.mapperDtoDomain.NutritionDtoDomain;
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

    private NutritionServiceImpl nutritionService;

    @BeforeEach
    void setUp() {
        nutritionService = new NutritionServiceImpl(nutritionRepository, new NutritionDtoDomain());
    }

    @Test
    void getNutritionByIdWhenMissingThrowsNotFound() {
        UUID nutritionId = UUID.randomUUID();
        when(nutritionRepository.getNutritionById(nutritionId)).thenReturn(Optional.empty());

        NutritionExistException ex = assertThrows(
                NutritionExistException.class,
                () -> nutritionService.getNutritionById(nutritionId)
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
    void updateNutritionWhenNotOwnedThrowsUnauthorized() {
        UUID nutritionId = UUID.randomUUID();
        Nutrition existing = validNutrition(nutritionId, "owner@example.com");
        UpdateNutritionRequest request = updateRequest(nutritionId);

        when(nutritionRepository.getNutritionById(nutritionId)).thenReturn(Optional.of(existing));
        when(nutritionRepository.isOwnedByUser(nutritionId, "other@example.com")).thenReturn(false);

        UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> nutritionService.updateNutrition(nutritionId, request, "other@example.com")
        );

        assertEquals("You cannot update this nutrition entry", ex.getMessage());
        verify(nutritionRepository, never()).save(any());
    }

    @Test
    void updateNutritionWhenOwnedSavesUpdatedNutrition() {
        UUID nutritionId = UUID.randomUUID();
        Nutrition existing = validNutrition(nutritionId, "owner@example.com");
        UpdateNutritionRequest request = updateRequest(nutritionId);

        when(nutritionRepository.getNutritionById(nutritionId)).thenReturn(Optional.of(existing));
        when(nutritionRepository.isOwnedByUser(nutritionId, "owner@example.com")).thenReturn(true);
        when(nutritionRepository.save(any(Nutrition.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Nutrition updated = nutritionService.updateNutrition(nutritionId, request, "owner@example.com");

        assertEquals("Updated meal", updated.getMealName());
        assertEquals(700, updated.getCalories());
        assertEquals(NutritionCategory.DINNER, updated.getCategory());
    }

    @Test
    void deleteNutritionWhenFoundDeletesById() {
        UUID nutritionId = UUID.randomUUID();
        when(nutritionRepository.getNutritionById(nutritionId))
                .thenReturn(Optional.of(validNutrition(nutritionId, "owner@example.com")));

        nutritionService.deleteNutrition(nutritionId);

        verify(nutritionRepository).delete(nutritionId);
    }

    private Nutrition validNutrition(UUID id, String userEmail) {
        LocalDate now = LocalDate.now();
        return new Nutrition(
                id,
                "Chicken Bowl",
                now,
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
