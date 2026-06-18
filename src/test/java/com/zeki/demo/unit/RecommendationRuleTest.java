package com.zeki.demo.unit;

import com.zeki.demo.workouthistory.domain.WorkoutHistory;
import com.zeki.demo.workouthistory.repository.WorkoutHistoryRepository;
import com.zeki.demo.recommendations.rules.DifficultyMatchRule;
import com.zeki.demo.recommendations.rules.GoalMatchRule;
import com.zeki.demo.recommendations.rules.MuscleBalanceRule;
import com.zeki.demo.recommendations.rules.PopularityRule;
import com.zeki.demo.recommendations.rules.RecentWorkoutRule;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.enums.ExperienceLevel;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.enums.WorkoutDifficulty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RecommendationRuleTest {
    private static final String EMAIL = "user@example.com";

    @Test
    void goalMatchRuleScoresMatchingWorkoutCategory() {
        assertEquals(50, new GoalMatchRule().calculate(user(), workout(WorkoutCategory.STRENGTH, MuscleType.LEGS)));
        assertEquals(0, new GoalMatchRule().calculate(user(), workout(WorkoutCategory.CARDIO, MuscleType.LEGS)));
    }

    @Test
    void muscleBalanceRuleScoresUndertrainedMusclesFromLastThirtyDays() {
        WorkoutHistoryRepository repository = mock(WorkoutHistoryRepository.class);
        MuscleBalanceRule rule = new MuscleBalanceRule(repository);
        when(repository.findAllByUserEmailSince(eq(EMAIL), any(LocalDate.class)))
                .thenReturn(List.of(
                        history(MuscleType.CHEST, LocalDate.now().minusDays(3)),
                        history(MuscleType.CHEST, LocalDate.now().minusDays(5))
                ));

        assertEquals(30, rule.calculate(user(), workout(WorkoutCategory.STRENGTH, MuscleType.LEGS)));
        assertEquals(0, rule.calculate(user(), workout(WorkoutCategory.STRENGTH, MuscleType.CHEST)));
    }

    @Test
    void recentWorkoutRulePenalizesRecentlyCompletedSimilarWorkout() {
        WorkoutHistoryRepository repository = mock(WorkoutHistoryRepository.class);
        RecentWorkoutRule rule = new RecentWorkoutRule(repository);
        when(repository.findAllByUserEmailSince(eq(EMAIL), any(LocalDate.class)))
                .thenReturn(List.of(history(MuscleType.LEGS, LocalDate.now().minusDays(1))));

        assertEquals(-40, rule.calculate(user(), workout(WorkoutCategory.STRENGTH, MuscleType.LEGS)));
        assertEquals(0, rule.calculate(user(), workout(WorkoutCategory.CARDIO, MuscleType.CHEST)));
    }

    @Test
    void difficultyMatchRuleScoresMatchingExperienceLevel() {
        DifficultyMatchRule rule = new DifficultyMatchRule();

        assertEquals(20, rule.calculate(user(), workout(WorkoutCategory.STRENGTH, MuscleType.LEGS)));
        assertEquals(0, rule.calculate(user(), workout(WorkoutCategory.STRENGTH, MuscleType.LEGS, WorkoutDifficulty.ADVANCED)));
    }

    @Test
    void popularityRuleCapsCompletionBonusAtTen() {
        WorkoutHistoryRepository repository = mock(WorkoutHistoryRepository.class);
        PopularityRule rule = new PopularityRule(repository);
        Workout workout = workout(WorkoutCategory.STRENGTH, MuscleType.LEGS);
        when(repository.countByWorkoutId(workout.getWorkoutId())).thenReturn(42L);

        assertEquals(10, rule.calculate(user(), workout));
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

    private Workout workout(WorkoutCategory category, MuscleType muscleType) {
        return workout(category, muscleType, WorkoutDifficulty.BEGINNER);
    }

    private Workout workout(WorkoutCategory category, MuscleType muscleType, WorkoutDifficulty difficulty) {
        LocalDate now = LocalDate.now();
        return new Workout(
                "Workout " + muscleType,
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
                difficulty,
                List.of(),
                now,
                now,
                "owner@example.com"
        );
    }

    private WorkoutHistory history(MuscleType muscleType, LocalDate date) {
        return new WorkoutHistory(
                UUID.randomUUID(),
                date,
                30,
                3,
                10,
                20.0,
                "History",
                "",
                "",
                WorkoutCategory.STRENGTH,
                muscleType,
                WorkoutDifficulty.BEGINNER,
                date,
                date,
                EMAIL
        );
    }
}
