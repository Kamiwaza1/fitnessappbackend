package com.zeki.demo.recommendations.rules;

import com.zeki.demo.workouthistory.domain.WorkoutHistory;
import com.zeki.demo.workouthistory.domain.repository.WorkoutHistoryRepository;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.enums.MuscleType;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MuscleBalanceRule implements WeightedRecommendationRule {
    private final WorkoutHistoryRepository workoutHistoryRepository;

    @Override
    public int calculate(User user, Workout workout) {
        List<WorkoutHistory> recentHistory = workoutHistoryRepository.findAllByUserEmailSince(
                user.getEmail(),
                LocalDate.now().minusDays(30)
        );
        if (recentHistory.isEmpty()) {
            return 30;
        }

        Map<MuscleType, Long> counts = new EnumMap<>(MuscleType.class);
        recentHistory.forEach(history -> counts.merge(history.getMuscleType(), 1L, Long::sum));
        double average = counts.values().stream().mapToLong(Long::longValue).average().orElse(0);
        long candidateCount = counts.getOrDefault(workout.getMuscleType(), 0L);
        return candidateCount < average ? 30 : 0;
    }

    @Override
    public double weight() {
        return 0.25;
    }

    @Override
    public int maxPositiveScore() {
        return 30;
    }

    @Override
    public Optional<String> explain(User user, Workout workout, int score) {
        if (score <= 0) {
            return Optional.empty();
        }
        return Optional.of("balances undertrained " + workout.getMuscleType().name().replace('_', ' ').toLowerCase());
    }
}
