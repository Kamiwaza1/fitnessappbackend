package com.zeki.demo.workouts.dto;

import com.zeki.demo.workouts.enums.WorkoutCategory;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class WorkoutResponse {
    private UUID workoutId;
    private LocalDate workoutDate;
    private Integer durationMinutes;
    private String workoutName;
    private String notes;
    private String description;
    private WorkoutCategory category;
    private LocalDate updatedAt;
    private LocalDate createdAt;
}
