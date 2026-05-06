package com.zeki.demo.progress.mapperDtoDomain;

import com.zeki.demo.progress.domain.ProgressInsights;
import com.zeki.demo.progress.domain.ProgressSummary;
import com.zeki.demo.progress.dto.ProgressInsightsResponse;
import com.zeki.demo.progress.dto.ProgressSummaryResponse;
import com.zeki.demo.workouts.mapperDtoDomain.WorkoutDtoDomain;
import org.springframework.stereotype.Component;

@Component
public class ProgressDtoDomain {

    private final WorkoutDtoDomain workoutDtoDomain;

    public ProgressDtoDomain(WorkoutDtoDomain workoutDtoDomain) {
        this.workoutDtoDomain = workoutDtoDomain;
    }

    public ProgressSummaryResponse toSummaryResponse(ProgressSummary progressSummary) {
        return new ProgressSummaryResponse(
                progressSummary.weekStartDate(),
                progressSummary.weekEndDate(),
                progressSummary.weeklyTrainingVolumeKg(),
                progressSummary.weeklyDurationMinutes(),
                progressSummary.weeklyWorkoutsCount(),
                progressSummary.recentActivity()
                        .stream()
                        .map(workoutDtoDomain::toResponse)
                        .toList()
        );
    }

    public ProgressInsightsResponse toInsightsResponse(ProgressInsights progressInsights) {
        return new ProgressInsightsResponse(
                progressInsights.weeklyVolumeTrend()
                        .stream()
                        .map(point -> new ProgressInsightsResponse.WeeklyVolumePoint(
                                point.weekLabel(),
                                point.trainingVolumeKg()
                        ))
                        .toList(),
                progressInsights.volumeByCategory()
                        .stream()
                        .map(point -> new ProgressInsightsResponse.CategoryVolumePoint(
                                point.category(),
                                point.trainingVolumeKg()
                        ))
                        .toList(),
                progressInsights.personalRecord() == null ? null : new ProgressInsightsResponse.PersonalRecord(
                        progressInsights.personalRecord().workoutName(),
                        progressInsights.personalRecord().weightKg(),
                        progressInsights.personalRecord().reps(),
                        progressInsights.personalRecord().sets(),
                        progressInsights.personalRecord().trainingVolumeKg(),
                        progressInsights.personalRecord().workoutDate()
                )
        );
    }
}
