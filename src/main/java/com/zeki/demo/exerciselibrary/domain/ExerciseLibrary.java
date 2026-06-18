package com.zeki.demo.exerciselibrary.domain;

import java.util.List;

public record ExerciseLibrary(
        List<ExerciseLibraryItem> results,
        Integer count,
        Integer total
) {
}
