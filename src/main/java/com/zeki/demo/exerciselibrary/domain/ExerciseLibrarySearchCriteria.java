package com.zeki.demo.exerciselibrary.domain;

public record ExerciseLibrarySearchCriteria(
        String q,
        Integer limit,
        Integer offset,
        Integer muscleId,
        Integer equipmentId,
        Integer categoryId
) {
    public ExerciseLibrarySearchCriteria {
        q = normalizeQuery(q);
        validateLimit(limit);
        validateOffset(offset);
        validatePositiveId(muscleId, "Muscle ID");
        validatePositiveId(equipmentId, "Equipment ID");
        validatePositiveId(categoryId, "Category ID");
    }

    private static String normalizeQuery(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.length() > 100) {
            throw new IllegalArgumentException("Search query cannot be longer than 100 characters");
        }
        return normalized.isBlank() ? null : normalized;
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

    private static void validatePositiveId(Integer value, String label) {
        if (value != null && value < 1) {
            throw new IllegalArgumentException(label + " must be at least 1");
        }
    }
}
