package com.zeki.demo.exerciseLibrary.entity;

import java.util.List;

public record ExerciseLibraryItemEntity(
        Integer id,
        String uuid,
        String name,
        String description,
        String descriptionSource,
        ExerciseReferenceEntity category,
        List<ExerciseMuscleEntity> muscles,
        List<ExerciseMuscleEntity> secondaryMuscles,
        List<ExerciseReferenceEntity> equipment,
        List<String> aliases,
        List<ExerciseImageEntity> images,
        List<ExerciseVideoEntity> videos,
        ExerciseLicenseEntity license,
        String licenseAuthor
) {
}
