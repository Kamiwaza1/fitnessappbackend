package com.zeki.demo.recommendations.rules;

import com.zeki.demo.users.domain.User;
import com.zeki.demo.workouts.domain.Workout;
import java.util.Optional;

public interface WeightedRecommendationRule extends RecommendationRule {
    double weight();

    int maxPositiveScore();

    default Optional<String> explain(User user, Workout workout, int score) {
        return Optional.empty();
    }
}
