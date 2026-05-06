package com.zeki.demo.WorkoutHistory.domain;

import com.zeki.demo.workouts.enums.WorkoutCategory;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WorkoutHistory {
    private final UUID workoutId;
    private final LocalDate workoutDate;
    private final Integer durationMinutes;
    private final Integer sets;
    private final Integer reps;
    private final Double weightKg;
    private final String workoutName;
    private final String notes;
    private final String description;
    private final WorkoutCategory category;
    private final LocalDate updatedAt;
    private final LocalDate createdAt;
    private final String userEmail;
}
