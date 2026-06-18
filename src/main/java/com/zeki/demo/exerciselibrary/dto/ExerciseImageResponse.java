package com.zeki.demo.exerciseLibrary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExerciseImageResponse(
        Integer id,
        String image,
        @JsonProperty("is_main")
        Boolean isMain
) {
}
