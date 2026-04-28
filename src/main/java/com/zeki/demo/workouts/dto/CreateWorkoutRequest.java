package com.zeki.demo.workouts.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateWorkoutRequest {

    @NotBlank
    private String workoutName;
    @NotNull
    private LocalDate workoutDate;
    @NotNull
    private Integer durationMinutes;
    private String notes;


    public CreateWorkoutRequest(String workoutName, LocalDate workoutDate, Integer durationMinutes, String notes) {

        this.workoutName = workoutName;
        this.workoutDate = workoutDate;
        this.durationMinutes = durationMinutes;
        this.notes = notes;
    }


}