package com.zeki.demo.progress.domain;

import com.zeki.demo.workouts.domain.Workout;
import java.time.LocalDate;
import java.util.List;

public record ProgressSummary(
        LocalDate weekStartDate,
        LocalDate weekEndDate,
        Double weeklyTrainingVolumeKg,
        Integer weeklyDurationMinutes,
        Integer weeklyWorkoutsCount,
        List<Workout> recentActivity
) {
}
