package com.zeki.demo.exerciseLibrary.entity;

public record ExerciseVideoEntity(
        Integer id,
        String video,
        Boolean isMain,
        String duration
) {
}
