package com.zeki.demo.progress.dto;

import com.zeki.demo.workouts.dto.WorkoutResponse;
import java.time.LocalDate;
import java.util.List;

public record ProgressSummaryResponse(
        LocalDate weekStartDate,
        LocalDate weekEndDate,
        Double weeklyTrainingVolumeKg,
        Integer weeklyDurationMinutes,
        Integer weeklyWorkoutsCount,
        List<WorkoutResponse> recentActivity
) {
}
