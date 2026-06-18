package com.zeki.demo.recommendations.rules;

import com.zeki.demo.users.domain.User;
import com.zeki.demo.workouts.domain.Workout;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class DifficultyMatchRule implements WeightedRecommendationRule {
    @Override
    public int calculate(User user, Workout workout) {
        return workout.getDifficulty().name().equals(user.getExperienceLevel().name()) ? 20 : 0;
    }

    @Override
    public double weight() {
        return 0.10;
    }

    @Override
    public int maxPositiveScore() {
        return 20;
    }

    @Override
    public Optional<String> explain(User user, Workout workout, int score) {
        if (score <= 0) {
            return Optional.empty();
        }
        return Optional.of("fits your " + user.getExperienceLevel().name().toLowerCase() + " experience level");
    }
}
