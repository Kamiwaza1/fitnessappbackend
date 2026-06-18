package com.zeki.demo.recommendations.mapperdtodomain;

import com.zeki.demo.recommendations.domain.WorkoutRecommendation;
import com.zeki.demo.recommendations.dto.WorkoutRecommendationResponse;
import org.springframework.stereotype.Component;

@Component
public class RecommendationDtoDomain {
    public WorkoutRecommendationResponse toResponse(WorkoutRecommendation recommendation) {
        return new WorkoutRecommendationResponse(
                recommendation.getWorkout().getWorkoutId(),
                recommendation.getWorkout().getWorkoutName(),
                recommendation.getScore(),
                recommendation.getReason()
        );
    }
}
