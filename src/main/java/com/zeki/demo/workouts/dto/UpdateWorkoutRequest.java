package com.zeki.demo.workouts.dto;

import com.zeki.demo.workouts.enums.WorkoutCategory;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateWorkoutRequest {
    @NotNull(message = "Workout name is required")
    private String workoutName;

    @NotNull(message = "Workout date is required")
    private LocalDate workoutDate;

    @NotNull(message = "Duration minutes is required")
    private Integer durationMinutes;

    private String notes;
    private WorkoutCategory category;
    private UUID workoutId;

    public UpdateWorkoutRequest(
            UUID workoutId,
            String workoutName,
            LocalDate workoutDate,
            Integer durationMinutes,
            String notes,
            WorkoutCategory category
    ) {
        this.workoutId = workoutId;
        this.workoutName = workoutName;
        this.workoutDate = workoutDate;
        this.durationMinutes = durationMinutes;
        this.notes = notes;
        this.category = category;
    }
}
