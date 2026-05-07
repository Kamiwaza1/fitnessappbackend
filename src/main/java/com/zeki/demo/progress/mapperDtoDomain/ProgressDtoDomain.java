package com.zeki.demo.progress.mapperDtoDomain;

import com.zeki.demo.progress.domain.Progress;
import com.zeki.demo.progress.dto.ProgressResponse;
import com.zeki.demo.workouts.mapperDtoDomain.WorkoutDtoDomain;
import org.springframework.stereotype.Component;

@Component
public class ProgressDtoDomain {

    private final WorkoutDtoDomain workoutDtoDomain;

    public ProgressDtoDomain(WorkoutDtoDomain workoutDtoDomain) {
        this.workoutDtoDomain = workoutDtoDomain;
    }

    public ProgressResponse toResponse(Progress progress) {
        return new ProgressResponse(
                progress.weekStartDate(),
                progress.weekEndDate(),
                progress.weeklyTrainingVolumeKg(),
                progress.weeklyDurationMinutes(),
                progress.weeklyWorkoutsCount(),
                progress.recentActivity()
                        .stream()
                        .map(workoutDtoDomain::toResponse)
                        .toList(),
                progress.weeklyVolumeTrend()
                        .stream()
                        .map(point -> new ProgressResponse.WeeklyVolumePoint(
                                point.weekLabel(),
                                point.trainingVolumeKg()
                        ))
                        .toList(),
                progress.volumeByCategory()
                        .stream()
                        .map(point -> new ProgressResponse.CategoryVolumePoint(
                                point.category(),
                                point.trainingVolumeKg()
                        ))
                        .toList(),
                progress.personalRecord() == null ? null : new ProgressResponse.PersonalRecord(
                        progress.personalRecord().workoutName(),
                        progress.personalRecord().weightKg(),
                        progress.personalRecord().reps(),
                        progress.personalRecord().sets(),
                        progress.personalRecord().trainingVolumeKg(),
                        progress.personalRecord().workoutDate()
                )
        );
    }
}
