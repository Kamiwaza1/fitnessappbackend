package com.zeki.demo.exerciseLibrary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExerciseMuscleResponse(
        Integer id,
        String name,
        @JsonProperty("name_en")
        String nameEn,
        @JsonProperty("is_front")
        Boolean isFront,
        @JsonProperty("image_url_main")
        String imageUrlMain,
        @JsonProperty("image_url_secondary")
        String imageUrlSecondary
) {
}
