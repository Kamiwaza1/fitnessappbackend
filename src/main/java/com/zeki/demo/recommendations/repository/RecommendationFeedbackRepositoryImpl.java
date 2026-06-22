package com.zeki.demo.recommendations.repository;

import com.zeki.demo.recommendations.domain.repository.RecommendationFeedbackRepository;
import com.zeki.demo.recommendations.domain.RecommendationFeedback;
import com.zeki.demo.recommendations.mapperentitydomain.RecommendationFeedbackEntityMapper;
import com.zeki.demo.recommendations.repository.jpa.RecommendationFeedbackRepoJPA;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RecommendationFeedbackRepositoryImpl implements RecommendationFeedbackRepository {
    private final RecommendationFeedbackRepoJPA repoJPA;
    private final RecommendationFeedbackEntityMapper mapper;

    @Override
    public List<RecommendationFeedback> findIgnoredByUserId(UUID userId) {
        return repoJPA.findAllByUserIdAndIgnoredTrue(userId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<RecommendationFeedback> findByUserIdAndWorkoutId(UUID userId, UUID workoutId) {
        return repoJPA.findByUserIdAndWorkoutId(userId, workoutId).map(mapper::toDomain);
    }

    @Override
    public RecommendationFeedback save(RecommendationFeedback feedback) {
        return mapper.toDomain(repoJPA.save(mapper.toEntity(feedback)));
    }
}
