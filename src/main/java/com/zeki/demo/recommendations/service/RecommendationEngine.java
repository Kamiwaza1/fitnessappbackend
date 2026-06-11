package com.zeki.demo.recommendations.service;

import com.zeki.demo.recommendations.domain.WorkoutRecommendation;
import com.zeki.demo.recommendations.domain.RecommendationFeedback;
import com.zeki.demo.recommendations.repository.RecommendationFeedbackRepository;
import com.zeki.demo.recommendations.rules.WeightedRecommendationRule;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.repository.WorkoutRepository;
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

        return workoutRepository.findAll()
                .stream()
                .filter(workout -> !ignoredWorkoutIds.contains(workout.getWorkoutId()))
                .map(workout -> toRecommendation(user, workout, maxWeightedScore))
                .sorted(Comparator.comparingInt(WorkoutRecommendation::getScore).reversed()
                        .thenComparing(recommendation -> recommendation.getWorkout().getWorkoutName()))
                .limit(RECOMMENDATION_LIMIT)
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
