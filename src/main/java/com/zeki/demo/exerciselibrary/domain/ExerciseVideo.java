package com.zeki.demo.exerciselibrary.domain;

public record ExerciseVideo(
        Integer id,
        String video,
        Boolean isMain,
        String duration
) {
}
