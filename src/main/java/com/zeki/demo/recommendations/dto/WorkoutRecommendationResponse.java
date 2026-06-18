package com.zeki.demo.recommendations.dto;

import java.util.UUID;

public record WorkoutRecommendationResponse(
        UUID workoutId,
        String workoutName,
        int score,
        String reason
) {
}
