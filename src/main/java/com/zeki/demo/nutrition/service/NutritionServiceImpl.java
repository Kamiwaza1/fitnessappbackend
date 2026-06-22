package com.zeki.demo.nutrition.service;

import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.nutrition.nutritionexception.NutritionExistException;
import com.zeki.demo.nutrition.domain.Nutrition;
import com.zeki.demo.nutrition.dto.FoodProductResponse;
import com.zeki.demo.nutrition.dto.FoodProductSearchRequest;
import com.zeki.demo.nutrition.dto.FoodProductsRequest;
import com.zeki.demo.nutrition.dto.FoodProductsResponse;
import com.zeki.demo.nutrition.dto.UpdateNutritionRequest;
import com.zeki.demo.nutrition.mapperdtodomain.NutritionDtoDomain;
import com.zeki.demo.nutrition.repository.FoodProductRepository;
import com.zeki.demo.nutrition.domain.repository.NutritionRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.DateTimeException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class NutritionServiceImpl implements NutritionService {

    private static final Pattern BARCODE_PATTERN = Pattern.compile("^[0-9]{4,32}$");
    private static final Pattern LANGUAGE_PATTERN = Pattern.compile("^[a-zA-Z]{2}$");

    private final NutritionRepository nutritionRepository;
    private final FoodProductRepository foodProductRepository;
    private final NutritionDtoDomain nutritionDtoDomain;

    @Override
    public Nutrition createNutrition(Nutrition nutrition, String timeZone) {
        ensureCurrentOrFutureMealDate(nutrition.getMealDate(), timeZone);
        nutrition.validate();
        return nutritionRepository.save(nutrition);
    }

    @Override
    public Nutrition getNutritionById(UUID nutritionId, String userEmail) {
        return nutritionRepository.getNutritionByIdAndUserEmail(nutritionId, userEmail)
                .orElseThrow(() -> new NutritionExistException("Nutrition entry not found"));
    }

    @Override
    public List<Nutrition> getMyNutritionEntries(String userEmail) {
        return nutritionRepository.findAllByUserEmail(userEmail);
    }

    @Override
    public FoodProductsResponse searchFoodProducts(FoodProductSearchRequest request) {
        validateSearchRequest(request);
        return foodProductRepository.searchProducts(request);
    }

    @Override
    public FoodProductResponse getFoodProductByBarcode(String barcode, String lang) {
        validateBarcode(barcode);
        validateLanguage(lang);
        return foodProductRepository.getProductByBarcode(barcode, lang);
    }

    @Override
    public FoodProductsResponse getFoodProductsByBrand(String brand, FoodProductsRequest request) {
        validateTagValue(brand, "Brand");
        validateProductsRequest(request);
        return foodProductRepository.getProductsByBrand(brand, request);
    }

    @Override
    public FoodProductsResponse getFoodProductsByCategory(String category, FoodProductsRequest request) {
        validateTagValue(category, "Category");
        validateProductsRequest(request);
        return foodProductRepository.getProductsByCategory(category, request);
    }

    @Override
    public Nutrition updateNutrition(UUID nutritionId, UpdateNutritionRequest request, String userEmail, String timeZone) {
        Nutrition existingNutrition = nutritionRepository.getNutritionByIdAndUserEmail(nutritionId, userEmail)
                .orElseThrow(() -> new NutritionExistException("Nutrition entry not found"));
        ensureEntryCanStillBeChanged(existingNutrition, timeZone);
        Nutrition updatedNutrition = nutritionDtoDomain.toUpdatedDomain(request, existingNutrition);
        ensureCurrentOrFutureMealDate(updatedNutrition.getMealDate(), timeZone);
        updatedNutrition.validate();
        return nutritionRepository.save(updatedNutrition);
    }

    @Override
    public void deleteNutrition(UUID nutritionId, String userEmail, String timeZone) {
        Nutrition existingNutrition = nutritionRepository.getNutritionByIdAndUserEmail(nutritionId, userEmail)
                .orElseThrow(() -> new NutritionExistException("Nutrition entry not found"));
        ensureEntryCanStillBeChanged(existingNutrition, timeZone);
        nutritionRepository.delete(nutritionId);
    }

    private void ensureEntryCanStillBeChanged(Nutrition nutrition, String timeZone) {
        if (nutrition.getMealDate() != null && nutrition.getMealDate().isBefore(today(timeZone))) {
            throw new BadRequestException("Past nutrition entries cannot be changed");
        }
    }

    private void ensureCurrentOrFutureMealDate(LocalDate mealDate, String timeZone) {
        if (mealDate != null && mealDate.isBefore(today(timeZone))) {
            throw new BadRequestException("Food cannot be added to a past day");
        }
    }

    private LocalDate today(String timeZone) {
        return LocalDate.now(resolveZone(timeZone));
    }

    private ZoneId resolveZone(String timeZone) {
        if (timeZone == null || timeZone.isBlank()) {
            return ZoneId.systemDefault();
        }
        try {
            return ZoneId.of(timeZone.trim());
        } catch (DateTimeException ex) {
            return ZoneId.systemDefault();
        }
    }

    private void validateSearchRequest(FoodProductSearchRequest request) {
        if (request == null) {
            throw new BadRequestException("Food search request is required");
        }
        validateTextLength(request.q(), "Search query", 2, 100, true);
        validatePaging(request.limit(), request.offset());
        validateLanguage(request.lang());
        validateOptionalMaxLength(request.countries(), "Countries", 100);
    }

    private void validateProductsRequest(FoodProductsRequest request) {
        if (request == null) {
            return;
        }
        validatePaging(request.limit(), request.offset());
        validateLanguage(request.lang());
        validateOptionalMaxLength(request.countries(), "Countries", 100);
    }

    private void validatePaging(Integer limit, Integer offset) {
        if (limit != null && (limit < 1 || limit > 100)) {
            throw new BadRequestException("Limit must be between 1 and 100");
        }
        if (offset != null && offset < 0) {
            throw new BadRequestException("Offset must be at least 0");
        }
    }

    private void validateBarcode(String barcode) {
        if (barcode == null || barcode.isBlank()) {
            throw new BadRequestException("Barcode is required");
        }
        if (!BARCODE_PATTERN.matcher(barcode.trim()).matches()) {
            throw new BadRequestException("Barcode must contain 4 to 32 digits");
        }
    }

    private void validateLanguage(String lang) {
        if (lang != null && !lang.isBlank() && !LANGUAGE_PATTERN.matcher(lang.trim()).matches()) {
            throw new BadRequestException("Language must be a two-letter code");
        }
    }

    private void validateTagValue(String value, String label) {
        validateTextLength(value, label, 1, 100, true);
    }

    private void validateTextLength(String value, String label, int min, int max, boolean required) {
        if (value == null || value.isBlank()) {
            if (required) {
                throw new BadRequestException(label + " is required");
            }
            return;
        }
        int length = value.trim().length();
        if (length < min || length > max) {
            throw new BadRequestException(label + " must be between " + min + " and " + max + " characters");
        }
    }

    private void validateOptionalMaxLength(String value, String label, int max) {
        if (value != null && value.trim().length() > max) {
            throw new BadRequestException(label + " cannot be longer than " + max + " characters");
        }
    }
}
