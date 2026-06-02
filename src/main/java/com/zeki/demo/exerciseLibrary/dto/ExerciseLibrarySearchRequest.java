package com.zeki.demo.exerciseLibrary.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ExerciseLibrarySearchRequest(
        String q,
        @Min(value = 1, message = "Limit must be at least 1")
        @Max(value = 100, message = "Limit cannot be greater than 100")
        Integer limit,
        @Min(value = 0, message = "Offset must be at least 0")
        Integer offset,
        @Min(value = 1, message = "Language must be at least 1")
        Integer language,
        @Min(value = 1, message = "Muscle ID must be at least 1")
        Integer muscleId,
        @Min(value = 1, message = "Equipment ID must be at least 1")
        Integer equipmentId,
        @Min(value = 1, message = "Category ID must be at least 1")
        Integer categoryId
) {
}
