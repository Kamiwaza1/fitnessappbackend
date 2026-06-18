package com.zeki.demo.exerciselibrary.dto;

import java.util.List;

public record ExerciseLibraryItemResponse(
        Integer id,
        String uuid,
        String name,
        String description,
        String descriptionSource,
        ExerciseReferenceResponse category,
        List<ExerciseMuscleResponse> muscles,
        List<ExerciseMuscleResponse> secondaryMuscles,
        List<ExerciseReferenceResponse> equipment,
        List<String> aliases,
        List<ExerciseImageResponse> images,
        List<ExerciseVideoResponse> videos,
        ExerciseLicenseResponse license,
        String licenseAuthor
) {
}
