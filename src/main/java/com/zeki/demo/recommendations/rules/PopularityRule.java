package com.zeki.demo.recommendations.rules;

import com.zeki.demo.workouthistory.domain.repository.WorkoutHistoryRepository;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.workouts.domain.Workout;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopularityRule implements WeightedRecommendationRule {
    private final WorkoutHistoryRepository workoutHistoryRepository;

    @Override
    public int calculate(User user, Workout workout) {
        return (int) Math.min(10, workoutHistoryRepository.countByWorkoutId(workout.getWorkoutId()));
    }

    @Override
    public double weight() {
        return 0.05;
    }

    @Override
    public int maxPositiveScore() {
        return 10;
    }

    @Override
    public Optional<String> explain(User user, Workout workout, int score) {
        if (score <= 0) {
            return Optional.empty();
        }
        return Optional.of("has been completed by other athletes");
    }
}
