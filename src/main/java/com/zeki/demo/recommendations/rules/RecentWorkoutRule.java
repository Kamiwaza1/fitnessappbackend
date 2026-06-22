package com.zeki.demo.recommendations.rules;

import com.zeki.demo.workouthistory.domain.WorkoutHistory;
import com.zeki.demo.workouthistory.domain.repository.WorkoutHistoryRepository;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.workouts.domain.Workout;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecentWorkoutRule implements WeightedRecommendationRule {
    private final WorkoutHistoryRepository workoutHistoryRepository;

    @Override
    public int calculate(User user, Workout workout) {
        List<WorkoutHistory> recentHistory = workoutHistoryRepository.findAllByUserEmailSince(
                user.getEmail(),
                LocalDate.now().minusDays(7)
        );
        boolean recentlyCompleted = recentHistory.stream().anyMatch(history ->
                workout.getWorkoutId().equals(history.getWorkoutId())
                        || (workout.getCategory() == history.getCategory()
                        && workout.getMuscleType() == history.getMuscleType())
        );
        return recentlyCompleted ? -40 : 0;
    }

    @Override
    public double weight() {
        return 0.20;
    }

    @Override
    public int maxPositiveScore() {
        return 0;
    }
}
