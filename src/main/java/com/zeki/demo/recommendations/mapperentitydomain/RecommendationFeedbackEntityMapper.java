package com.zeki.demo.recommendations.mapperentitydomain;

import com.zeki.demo.recommendations.domain.RecommendationFeedback;
import com.zeki.demo.recommendations.entity.RecommendationFeedbackEntity;
import org.springframework.stereotype.Component;

@Component
public class RecommendationFeedbackEntityMapper {
    public RecommendationFeedback toDomain(RecommendationFeedbackEntity entity) {
        return new RecommendationFeedback(
                entity.getId(),
                entity.getUserId(),
                entity.getWorkoutId(),
                entity.isIgnored(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public RecommendationFeedbackEntity toEntity(RecommendationFeedback feedback) {
        return RecommendationFeedbackEntity.builder()
                .id(feedback.getId())
                .userId(feedback.getUserId())
                .workoutId(feedback.getWorkoutId())
                .ignored(feedback.isIgnored())
                .createdAt(feedback.getCreatedAt())
                .updatedAt(feedback.getUpdatedAt())
                .build();
    }
}
