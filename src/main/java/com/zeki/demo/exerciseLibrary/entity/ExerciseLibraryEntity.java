package com.zeki.demo.exerciseLibrary.entity;

import java.util.List;

public record ExerciseLibraryEntity(
        List<ExerciseLibraryItemEntity> results,
        Integer count,
        Integer total
) {
}
