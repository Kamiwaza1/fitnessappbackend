package com.zeki.demo.progress.dto;

import java.util.List;

public record ProgressInsightsResponse(
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
            String category,
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
