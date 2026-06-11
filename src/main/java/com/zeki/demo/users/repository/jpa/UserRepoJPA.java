package com.zeki.demo.users.repository.jpa;

import com.zeki.demo.users.entity.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepoJPA extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmailIgnoreCase(String email);
    Optional<UserEntity> findByEmailIgnoreCaseAndIsActiveTrue(String email);
    List<UserEntity> findAllByDeletionScheduledForLessThanEqual(LocalDateTime deletionScheduledFor);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIsActiveTrue(String email);

    @Modifying
    @Query(value = "DELETE FROM live_workout_participants WHERE email = :email", nativeQuery = true)
    void deleteLiveWorkoutParticipantsByEmail(@Param("email") String email);

    @Modifying
    @Query(value = "DELETE FROM workout_history WHERE user_email = :email", nativeQuery = true)
    void deleteWorkoutHistoryByUserEmail(@Param("email") String email);

    @Modifying
    @Query(value = "DELETE FROM nutrition_entries WHERE user_id = :userId", nativeQuery = true)
    void deleteNutritionEntriesByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query(value = "DELETE FROM workouts WHERE user_id = :userId", nativeQuery = true)
    void deleteWorkoutsByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query(value = "DELETE FROM recommendation_feedback WHERE user_id = :userId", nativeQuery = true)
    void deleteRecommendationFeedbackByUserId(@Param("userId") UUID userId);
}
