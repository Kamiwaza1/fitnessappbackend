package com.zeki.demo.recommendations.service;

import com.zeki.demo.recommendations.domain.WorkoutRecommendation;
import com.zeki.demo.workouts.domain.Workout;
import java.util.List;
import java.util.UUID;

public interface RecommendationService {
    List<WorkoutRecommendation> getRecommendations(String userEmail);

    void ignoreRecommendation(UUID workoutId, String userEmail);

    Workout startRecommendation(UUID workoutId, String userEmail);
}
