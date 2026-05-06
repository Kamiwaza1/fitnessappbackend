package com.zeki.demo.progress.service;

import com.zeki.demo.progress.domain.ProgressInsights;
import com.zeki.demo.progress.domain.ProgressSummary;
import com.zeki.demo.progress.repository.ProgressRepository;
import com.zeki.demo.workouts.domain.Workout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService {

    private final ProgressRepository progressRepository;

    @Override
    public ProgressSummary getProgressSummary(String userEmail) {
        List<Workout> workouts = progressRepository.findAllByUserEmail(userEmail);
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(WeekFields.ISO.dayOfWeek(), 1);
        LocalDate weekEnd = weekStart.plusDays(6);

        List<Workout> currentWeekWorkouts = workouts.stream()
                .filter(workout -> !workout.getWorkoutDate().isBefore(weekStart))
                .filter(workout -> !workout.getWorkoutDate().isAfter(weekEnd))
                .toList();

        double weeklyTrainingVolumeKg = currentWeekWorkouts.stream()
                .mapToDouble(this::calculateVolume)
                .sum();

        int weeklyDurationMinutes = currentWeekWorkouts.stream()
                .mapToInt(Workout::getDurationMinutes)
                .sum();

        List<Workout> recentActivity = workouts.stream()
                .filter(workout -> !workout.getWorkoutDate().isAfter(today))
                .sorted(Comparator.comparing(Workout::getWorkoutDate).reversed())
                .limit(10)
                .toList();

        return new ProgressSummary(
                weekStart,
                weekEnd,
                weeklyTrainingVolumeKg,
                weeklyDurationMinutes,
                currentWeekWorkouts.size(),
                recentActivity
        );
    }

    @Override
    public ProgressInsights getProgressInsights(String userEmail) {
        List<Workout> workouts = progressRepository.findAllByUserEmail(userEmail);
        LocalDate today = LocalDate.now();
        LocalDate currentWeekStart = today.with(WeekFields.ISO.dayOfWeek(), 1);
        DateTimeFormatter labelFormatter = DateTimeFormatter.ofPattern("MMM d");

        Map<LocalDate, Double> volumeByWeek = workouts.stream()
                .collect(Collectors.groupingBy(
                        workout -> workout.getWorkoutDate().with(WeekFields.ISO.dayOfWeek(), 1),
                        Collectors.summingDouble(this::calculateVolume)
                ));

        List<ProgressInsights.WeeklyVolumePoint> weeklyTrend = IntStream.rangeClosed(0, 3)
                .mapToObj(index -> currentWeekStart.minusWeeks(3 - index))
                .map(weekStart -> new ProgressInsights.WeeklyVolumePoint(
                        weekStart.format(labelFormatter),
                        volumeByWeek.getOrDefault(weekStart, 0.0)
                ))
                .toList();

        List<ProgressInsights.CategoryVolumePoint> categoryBreakdown = workouts.stream()
                .collect(Collectors.groupingBy(
                        workout -> workout.getCategory().name(),
                        Collectors.summingDouble(this::calculateVolume)
                ))
                .entrySet()
                .stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .map(entry -> new ProgressInsights.CategoryVolumePoint(entry.getKey(), entry.getValue()))
                .toList();

        ProgressInsights.PersonalRecord personalRecord = workouts.stream()
                .max(Comparator.comparingDouble(this::calculateVolume))
                .map(workout -> new ProgressInsights.PersonalRecord(
                        workout.getWorkoutName(),
                        workout.getWeightKg(),
                        workout.getReps(),
                        workout.getSets(),
                        calculateVolume(workout),
                        workout.getWorkoutDate().toString()
                ))
                .orElse(null);

        return new ProgressInsights(weeklyTrend, categoryBreakdown, personalRecord);
    }

    private double calculateVolume(Workout workout) {
        return workout.getWeightKg() * workout.getSets() * workout.getReps();
    }
}
