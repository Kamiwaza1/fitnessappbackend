package com.zeki.demo.exerciseLibrary.domain;

public record ExerciseLibrarySearchCriteria(
        String q,
        Integer limit,
        Integer offset,
        Integer muscleId,
        Integer equipmentId,
        Integer categoryId
) {
}
