package com.zeki.demo.exerciseLibrary.domain;

public record ExerciseLibrarySearchCriteria(
        String q,
        Integer limit,
        Integer offset,
        Integer language,
        Integer muscleId,
        Integer equipmentId,
        Integer categoryId
) {
}
