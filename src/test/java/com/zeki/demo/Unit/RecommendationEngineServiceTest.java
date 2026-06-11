package com.zeki.demo.Unit;

import com.zeki.demo.recommendations.domain.RecommendationFeedback;
import com.zeki.demo.recommendations.domain.WorkoutRecommendation;
import com.zeki.demo.recommendations.repository.RecommendationFeedbackRepository;
import com.zeki.demo.recommendations.rules.GoalMatchRule;
import com.zeki.demo.recommendations.rules.WeightedRecommendationRule;
import com.zeki.demo.recommendations.service.RecommendationEngine;
import com.zeki.demo.recommendations.service.RecommendationServiceImpl;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.enums.ExperienceLevel;
import com.zeki.demo.users.service.UserService;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.enums.WorkoutDifficulty;
import com.zeki.demo.workouts.repository.WorkoutRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecommendationEngineServiceTest {
    private static final String EMAIL = "user@example.com";

    @Test
    void engineExcludesIgnoredWorkoutsSortsByScoreAndLimitsToFive() {
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        RecommendationFeedbackRepository feedbackRepository = mock(RecommendationFeedbackRepository.class);
        User user = user();
        Workout ignored = workout("Ignored", WorkoutCategory.STRENGTH, MuscleType.LEGS, "other@example.com");
        Workout matching = workout("Match", WorkoutCategory.STRENGTH, MuscleType.CHEST, "other@example.com");
        Workout nonMatching = workout("Other", WorkoutCategory.CARDIO, MuscleType.BACK, "other@example.com");

        when(feedbackRepository.findIgnoredByUserId(user.getUserId()))
                .thenReturn(List.of(new RecommendationFeedback(null, user.getUserId(), ignored.getWorkoutId(), true, null, null)));
        when(workoutRepository.findAll()).thenReturn(List.of(nonMatching, ignored, matching));

        List<WeightedRecommendationRule> rules = List.of(new GoalMatchRule());
        RecommendationEngine engine = new RecommendationEngine(workoutRepository, feedbackRepository, rules);

        List<WorkoutRecommendation> recommendations = engine.recommend(user);

        assertEquals(2, recommendations.size());
        assertEquals(matching.getWorkoutId(), recommendations.get(0).getWorkout().getWorkoutId());
        assertTrue(recommendations.stream().noneMatch(item -> item.getWorkout().getWorkoutId().equals(ignored.getWorkoutId())));
    }

    @Test
    void startRecommendationCopiesWorkoutOwnedByAnotherUser() {
        UserService userService = mock(UserService.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        RecommendationFeedbackRepository feedbackRepository = mock(RecommendationFeedbackRepository.class);
        RecommendationEngine engine = mock(RecommendationEngine.class);
        RecommendationServiceImpl service = new RecommendationServiceImpl(userService, workoutRepository, feedbackRepository, engine);
        Workout source = workout("Shared Strength", WorkoutCategory.STRENGTH, MuscleType.LEGS, "owner@example.com");
        Workout saved = workout("Shared Strength", WorkoutCategory.STRENGTH, MuscleType.LEGS, EMAIL);

        when(workoutRepository.getWorkoutById(source.getWorkoutId())).thenReturn(Optional.of(source));
        when(workoutRepository.save(any(Workout.class))).thenReturn(saved);

        Workout result = service.startRecommendation(source.getWorkoutId(), EMAIL);

        assertEquals(saved, result);
        verify(workoutRepository).save(any(Workout.class));
    }

    @Test
    void startRecommendationReturnsOwnWorkoutWithoutCopying() {
        UserService userService = mock(UserService.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        RecommendationFeedbackRepository feedbackRepository = mock(RecommendationFeedbackRepository.class);
        RecommendationEngine engine = mock(RecommendationEngine.class);
        RecommendationServiceImpl service = new RecommendationServiceImpl(userService, workoutRepository, feedbackRepository, engine);
        Workout ownWorkout = workout("Own Strength", WorkoutCategory.STRENGTH, MuscleType.LEGS, EMAIL);

        when(workoutRepository.getWorkoutById(ownWorkout.getWorkoutId())).thenReturn(Optional.of(ownWorkout));

        Workout result = service.startRecommendation(ownWorkout.getWorkoutId(), EMAIL);

        assertEquals(ownWorkout, result);
        assertNotEquals("owner@example.com", result.getUserEmail());
    }

    private User user() {
        return new User(
                UUID.randomUUID(),
                "Test User",
                EMAIL,
                "EncodedPassword123!",
                null,
                WorkoutCategory.STRENGTH,
                ExperienceLevel.BEGINNER,
                "USER",
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                null
        );
    }

    private Workout workout(String name, WorkoutCategory category, MuscleType muscleType, String userEmail) {
        LocalDate now = LocalDate.now();
        return new Workout(
                name,
                now,
                30,
                3,
                10,
                20.0,
                "",
                UUID.randomUUID(),
                "",
                category,
                muscleType,
                WorkoutDifficulty.BEGINNER,
                List.of(),
                now,
                now,
                userEmail
        );
    }
}
