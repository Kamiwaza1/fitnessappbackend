package com.zeki.demo.exerciselibrary.entity;

public record ExerciseVideoEntity(
        Integer id,
        String video,
        Boolean isMain,
        String duration
) {
}
