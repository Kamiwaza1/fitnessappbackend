package com.zeki.demo.recommendations.service;

import com.zeki.demo.workouthistory.domain.WorkoutHistory;
import com.zeki.demo.workouthistory.repository.WorkoutHistoryRepository;
import com.zeki.demo.recommendations.domain.WorkoutRecommendation;
import com.zeki.demo.recommendations.domain.RecommendationFeedback;
import com.zeki.demo.recommendations.repository.RecommendationFeedbackRepository;
import com.zeki.demo.recommendations.rules.WeightedRecommendationRule;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.repository.WorkoutRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecommendationEngine {
    private static final int RECOMMENDATION_LIMIT = 5;

    private final WorkoutRepository workoutRepository;
    private final WorkoutHistoryRepository workoutHistoryRepository;
    private final RecommendationFeedbackRepository feedbackRepository;
    private final List<WeightedRecommendationRule> rules;

    public List<WorkoutRecommendation> recommend(User user) {
        Set<UUID> ignoredWorkoutIds = feedbackRepository.findIgnoredByUserId(user.getUserId())
                .stream()
                .map(RecommendationFeedback::getWorkoutId)
                .collect(Collectors.toSet());

        double maxWeightedScore = rules.stream()
                .mapToDouble(rule -> rule.maxPositiveScore() * rule.weight())
                .sum();

        List<Workout> workouts = workoutRepository.findAll()
                .stream()
                .filter(workout -> !ignoredWorkoutIds.contains(workout.getWorkoutId()))
                .toList();
        List<Workout> eligibleWorkouts = excludeRecentlyTrainedMuscles(user, workouts);

        return eligibleWorkouts
                .stream()
                .map(workout -> toRecommendation(user, workout, maxWeightedScore))
                .sorted(Comparator.comparingInt(WorkoutRecommendation::getScore).reversed()
                        .thenComparing(recommendation -> recommendation.getWorkout().getWorkoutName()))
                .limit(RECOMMENDATION_LIMIT)
                .toList();
    }

    private List<Workout> excludeRecentlyTrainedMuscles(User user, List<Workout> workouts) {
        List<WorkoutHistory> recentHistory = workoutHistoryRepository.findAllByUserEmailSince(
                        user.getEmail(),
                        LocalDate.now().minusDays(7)
                );
        Set<MuscleType> recentlyTrainedMuscles = recentHistory
                .stream()
                .map(WorkoutHistory::getMuscleType)
                .collect(Collectors.toSet());
        Set<UUID> recentlyCompletedWorkoutIds = recentHistory
                .stream()
                .map(WorkoutHistory::getWorkoutId)
                .collect(Collectors.toSet());
        if (recentlyTrainedMuscles.isEmpty() && recentlyCompletedWorkoutIds.isEmpty()) {
            return workouts;
        }

        List<Workout> filteredWorkouts = workouts.stream()
                .filter(workout -> !recentlyCompletedWorkoutIds.contains(workout.getWorkoutId()))
                .filter(workout -> !recentlyTrainedMuscles.contains(workout.getMuscleType()))
                .toList();
        if (!filteredWorkouts.isEmpty()) {
            return filteredWorkouts;
        }
        return workouts.stream()
                .filter(workout -> !recentlyCompletedWorkoutIds.contains(workout.getWorkoutId()))
                .toList();
    }

    private WorkoutRecommendation toRecommendation(User user, Workout workout, double maxWeightedScore) {
        double weightedScore = 0;
        List<String> explanations = new ArrayList<>();

        for (WeightedRecommendationRule rule : rules) {
            int ruleScore = rule.calculate(user, workout);
            weightedScore += ruleScore * rule.weight();
            rule.explain(user, workout, ruleScore).ifPresent(explanations::add);
        }

        int normalizedScore = maxWeightedScore <= 0
                ? 0
                : (int) Math.round(Math.max(0, Math.min(100, (weightedScore / maxWeightedScore) * 100)));
        return new WorkoutRecommendation(workout, normalizedScore, explanations);
    }
}
