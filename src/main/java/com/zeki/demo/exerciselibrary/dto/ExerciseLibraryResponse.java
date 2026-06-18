package com.zeki.demo.exerciselibrary.dto;

import java.util.List;

public record ExerciseLibraryResponse(
        List<ExerciseLibraryItemResponse> results,
        Integer count,
        Integer total
) {
}
