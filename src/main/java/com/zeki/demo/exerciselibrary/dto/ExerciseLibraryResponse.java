package com.zeki.demo.exerciseLibrary.dto;

import java.util.List;

public record ExerciseLibraryResponse(
        List<ExerciseLibraryItemResponse> results,
        Integer count,
        Integer total
) {
}
