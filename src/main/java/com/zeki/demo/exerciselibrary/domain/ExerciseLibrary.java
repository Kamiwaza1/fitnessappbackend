package com.zeki.demo.exerciseLibrary.domain;

import java.util.List;

public record ExerciseLibrary(
        List<ExerciseLibraryItem> results,
        Integer count,
        Integer total
) {
}
