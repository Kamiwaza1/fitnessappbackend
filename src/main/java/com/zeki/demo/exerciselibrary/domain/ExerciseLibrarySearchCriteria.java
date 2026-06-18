package com.zeki.demo.exerciselibrary.domain;

public record ExerciseLibrarySearchCriteria(
        String q,
        Integer limit,
        Integer offset,
        Integer muscleId,
        Integer equipmentId,
        Integer categoryId
) {
}
