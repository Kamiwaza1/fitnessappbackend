package com.zeki.demo.exerciselibrary.domain;

import java.util.List;

public record ExerciseLibraryItem(
        Integer id,
        String uuid,
        String name,
        String description,
        String descriptionSource,
        ExerciseReference category,
        List<ExerciseMuscle> muscles,
        List<ExerciseMuscle> secondaryMuscles,
        List<ExerciseReference> equipment,
        List<String> aliases,
        List<ExerciseImage> images,
        List<ExerciseVideo> videos,
        ExerciseLicense license,
        String licenseAuthor
) {
}
