package com.zeki.demo.exerciselibrary.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ExerciseLibraryListRequest(
        @Min(value = 1, message = "Limit must be at least 1")
        @Max(value = 100, message = "Limit cannot be greater than 100")
        Integer limit,
        @Min(value = 0, message = "Offset must be at least 0")
        Integer offset
) {
}
