package com.zeki.demo.workouts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class WorkoutResponse {
    private Long id;
    private String workoutTitle;
    private LocalDate workoutDate;
    private Integer durationMinutes;
    private String notes;
    private String primaryGoal;
    private List<Exercise> exerciseLibrary;

}
