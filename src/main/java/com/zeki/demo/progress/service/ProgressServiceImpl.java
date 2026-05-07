package com.zeki.demo.progress.service;

import com.zeki.demo.progress.domain.Progress;
import com.zeki.demo.progress.repository.ProgressRepository;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.domain.WorkoutExercise;
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
    public Progress getProgress(String userEmail) {
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

        LocalDate currentWeekStart = today.with(WeekFields.ISO.dayOfWeek(), 1);
        DateTimeFormatter labelFormatter = DateTimeFormatter.ofPattern("MMM d");

        Map<LocalDate, Double> volumeByWeek = workouts.stream()
                .collect(Collectors.groupingBy(
                        workout -> workout.getWorkoutDate().with(WeekFields.ISO.dayOfWeek(), 1),
                        Collectors.summingDouble(this::calculateVolume)
                ));

        List<Progress.WeeklyVolumePoint> weeklyTrend = IntStream.rangeClosed(0, 3)
                .mapToObj(index -> currentWeekStart.minusWeeks(3 - index))
                .map(week -> new Progress.WeeklyVolumePoint(
                        week.format(labelFormatter),
                        volumeByWeek.getOrDefault(week, 0.0)
                ))
                .toList();

        List<Progress.CategoryVolumePoint> categoryBreakdown = workouts.stream()
                .collect(Collectors.groupingBy(
                        Workout::getCategory,
                        Collectors.summingDouble(this::calculateVolume)
                ))
                .entrySet()
                .stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .map(entry -> new Progress.CategoryVolumePoint(entry.getKey(), entry.getValue()))
                .toList();

        Progress.PersonalRecord personalRecord = workouts.stream()
                .max(Comparator.comparingDouble(this::calculateVolume))
                .map(workout -> new Progress.PersonalRecord(
                        workout.getWorkoutName(),
                        resolvePrWeight(workout),
                        resolvePrReps(workout),
                        resolvePrSets(workout),
                        calculateVolume(workout),
                        workout.getWorkoutDate().toString()
                ))
                .orElse(null);

        return new Progress(
                weekStart,
                weekEnd,
                weeklyTrainingVolumeKg,
                weeklyDurationMinutes,
                currentWeekWorkouts.size(),
                recentActivity,
                weeklyTrend,
                categoryBreakdown,
                personalRecord
        );
    }

    private double calculateVolume(Workout workout) {
        if (workout.getExercises() != null && !workout.getExercises().isEmpty()) {
            return workout.getExercises().stream()
                    .mapToDouble(exercise -> exercise.getWeightKg() * exercise.getSets() * exercise.getReps())
                    .sum();
        }
        return workout.getWeightKg() * workout.getSets() * workout.getReps();
    }

    private Double resolvePrWeight(Workout workout) {
        if (workout.getExercises() == null || workout.getExercises().isEmpty()) {
            return workout.getWeightKg();
        }
        return workout.getExercises().stream()
                .mapToDouble(WorkoutExercise::getWeightKg)
                .max()
                .orElse(0.0);
    }

    private Integer resolvePrReps(Workout workout) {
        if (workout.getExercises() == null || workout.getExercises().isEmpty()) {
            return workout.getReps();
        }
        return workout.getExercises().stream()
                .mapToInt(WorkoutExercise::getReps)
                .max()
                .orElse(0);
    }

    private Integer resolvePrSets(Workout workout) {
        if (workout.getExercises() == null || workout.getExercises().isEmpty()) {
            return workout.getSets();
        }
        return workout.getExercises().stream()
                .mapToInt(WorkoutExercise::getSets)
                .max()
                .orElse(0);
    }
}
