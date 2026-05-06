package com.zeki.demo.workouts.dto;

import java.time.LocalDate;
import java.util.List;

public record WorkoutProgressResponse(
        LocalDate weekStartDate,
        LocalDate weekEndDate,
        Double weeklyTrainingVolumeKg,
        Integer weeklyDurationMinutes,
        Integer weeklyWorkoutsCount,
        List<WorkoutResponse> recentActivity
) {
}
