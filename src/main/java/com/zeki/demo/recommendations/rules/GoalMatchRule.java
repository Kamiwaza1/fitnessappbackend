package com.zeki.demo.recommendations.rules;

import com.zeki.demo.users.domain.User;
import com.zeki.demo.workouts.domain.Workout;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class GoalMatchRule implements WeightedRecommendationRule {
    @Override
    public int calculate(User user, Workout workout) {
        return workout.getCategory() == user.getFitnessGoal() ? 50 : 0;
    }

    @Override
    public double weight() {
        return 0.40;
    }

    @Override
    public int maxPositiveScore() {
        return 50;
    }

    @Override
    public Optional<String> explain(User user, Workout workout, int score) {
        if (score <= 0) {
            return Optional.empty();
        }
        return Optional.of("matches your " + user.getFitnessGoal().name().replace('_', ' ').toLowerCase() + " goal");
    }
}
