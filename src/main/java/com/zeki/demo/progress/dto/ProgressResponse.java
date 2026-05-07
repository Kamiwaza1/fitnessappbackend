package com.zeki.demo.progress.dto;

import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.dto.WorkoutResponse;
import java.time.LocalDate;
import java.util.List;

public record ProgressResponse(
        LocalDate weekStartDate,
        LocalDate weekEndDate,
        Double weeklyTrainingVolumeKg,
        Integer weeklyDurationMinutes,
        Integer weeklyWorkoutsCount,
        List<WorkoutResponse> recentActivity,
        List<WeeklyVolumePoint> weeklyVolumeTrend,
        List<CategoryVolumePoint> volumeByCategory,
        PersonalRecord personalRecord
) {
    public record WeeklyVolumePoint(
            String weekLabel,
            Double trainingVolumeKg
    ) {
    }

    public record CategoryVolumePoint(
            WorkoutCategory category,
            Double trainingVolumeKg
    ) {
    }

    public record PersonalRecord(
            String workoutName,
            Double weightKg,
            Integer reps,
            Integer sets,
            Double trainingVolumeKg,
            String workoutDate
    ) {
    }
}
