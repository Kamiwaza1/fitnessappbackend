package com.zeki.demo.exerciselibrary.domain;

public record ExerciseLibraryListCriteria(
        Integer limit,
        Integer offset
) {
    public ExerciseLibraryListCriteria {
        validateLimit(limit);
        validateOffset(offset);
    }

    private static void validateLimit(Integer limit) {
        if (limit != null && (limit < 1 || limit > 100)) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }
    }

    private static void validateOffset(Integer offset) {
        if (offset != null && offset < 0) {
            throw new IllegalArgumentException("Offset must be at least 0");
        }
    }
}
