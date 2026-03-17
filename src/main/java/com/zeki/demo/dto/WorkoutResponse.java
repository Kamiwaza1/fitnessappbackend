package com.zeki.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class WorkoutResponse {
    private Long id;
    private String name;
    private LocalDate workoutDate;
    private Integer durationMinutes;
    private String notes;
}
