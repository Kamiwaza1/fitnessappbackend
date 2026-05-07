package com.zeki.demo.progress.domain;

import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.enums.WorkoutCategory;
import java.time.LocalDate;
import java.util.List;

public record Progress(
        LocalDate weekStartDate,
        LocalDate weekEndDate,
        Double weeklyTrainingVolumeKg,
        Integer weeklyDurationMinutes,
        Integer weeklyWorkoutsCount,
        List<Workout> recentActivity,
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
