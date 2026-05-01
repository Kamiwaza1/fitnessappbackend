package com.zeki.demo.workouts.dto;

import com.zeki.demo.workouts.enums.WorkoutCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateWorkoutRequest {

    @NotBlank(message = "Workout name is required")
    private String workoutName;

    @NotNull(message = "Workout date is required")
    private LocalDate workoutDate;

    @NotNull(message = "Duration minutes is required")
    private Integer durationMinutes;

    private String notes;
    private String description;
    private WorkoutCategory category;
    private UUID workoutId;

    public CreateWorkoutRequest(
            String workoutName,
            LocalDate workoutDate,
            Integer durationMinutes,
            String notes,
            String description,
            WorkoutCategory category,
            UUID workoutId
    ) {
        this.workoutName = workoutName;
        this.workoutDate = workoutDate;
        this.durationMinutes = durationMinutes;
        this.notes = notes;
        this.description = description;
        this.category = category;
        this.workoutId = workoutId;
    }
}
