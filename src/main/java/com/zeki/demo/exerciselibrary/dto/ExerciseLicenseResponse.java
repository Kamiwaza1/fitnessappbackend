package com.zeki.demo.exerciseLibrary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExerciseLicenseResponse(
        Integer id,
        @JsonProperty("full_name")
        String fullName,
        @JsonProperty("short_name")
        String shortName,
        String url
) {
}
