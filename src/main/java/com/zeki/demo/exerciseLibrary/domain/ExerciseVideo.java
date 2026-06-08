package com.zeki.demo.exerciseLibrary.domain;

public record ExerciseVideo(
        Integer id,
        String video,
        Boolean isMain,
        String duration
) {
}
