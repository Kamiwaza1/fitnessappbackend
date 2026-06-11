package com.zeki.demo.recommendations.domain;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class RecommendationFeedback {
    private final UUID id;
    private final UUID userId;
    private final UUID workoutId;
    private final boolean ignored;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public RecommendationFeedback(
            UUID id,
            UUID userId,
            UUID workoutId,
            boolean ignored,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.workoutId = workoutId;
        this.ignored = ignored;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
