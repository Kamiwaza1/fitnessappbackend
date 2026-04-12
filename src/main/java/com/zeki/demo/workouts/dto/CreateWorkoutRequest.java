package com.zeki.demo.workouts.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateWorkoutRequest {

    @NotBlank
    private String name;

    @NotNull
    private LocalDate workoutDate;

    @NotNull
    @Min(1)
    private Integer durationMinutes;

    private String notes;
}
