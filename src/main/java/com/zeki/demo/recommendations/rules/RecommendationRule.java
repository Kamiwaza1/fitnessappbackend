package com.zeki.demo.recommendations.rules;

import com.zeki.demo.users.domain.User;
import com.zeki.demo.workouts.domain.Workout;

public interface RecommendationRule {
    int calculate(User user, Workout workout);
}
