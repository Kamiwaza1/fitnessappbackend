package com.zeki.demo.recommendations.repository.jpa;

import com.zeki.demo.recommendations.entity.RecommendationFeedbackEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationFeedbackRepoJPA extends JpaRepository<RecommendationFeedbackEntity, UUID> {
    List<RecommendationFeedbackEntity> findAllByUserIdAndIgnoredTrue(UUID userId);

    Optional<RecommendationFeedbackEntity> findByUserIdAndWorkoutId(UUID userId, UUID workoutId);
}
