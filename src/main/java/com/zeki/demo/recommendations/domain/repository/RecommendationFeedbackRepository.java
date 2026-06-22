package com.zeki.demo.recommendations.domain.repository;

import com.zeki.demo.recommendations.domain.RecommendationFeedback;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecommendationFeedbackRepository {
    List<RecommendationFeedback> findIgnoredByUserId(UUID userId);

    Optional<RecommendationFeedback> findByUserIdAndWorkoutId(UUID userId, UUID workoutId);

    RecommendationFeedback save(RecommendationFeedback feedback);
}
