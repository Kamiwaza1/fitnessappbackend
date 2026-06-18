package com.zeki.demo.exerciselibrary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExerciseVideoResponse(
        Integer id,
        String video,
        @JsonProperty("is_main")
        Boolean isMain,
        String duration
) {
}
