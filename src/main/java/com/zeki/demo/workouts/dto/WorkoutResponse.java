package com.zeki.demo.workouts.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.zeki.demo.exerciseLibrary.dto.exerciseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@Builder
public class WorkoutResponse {
    private UUID id;
    private String workoutTitle;
    private LocalDate workoutDate;
    private Integer durationMinutes;
    private String notes;
    private String primaryGoal;
    private List<exerciseDto> exerciseLibrary;

}
