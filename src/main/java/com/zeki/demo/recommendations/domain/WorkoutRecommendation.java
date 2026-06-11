package com.zeki.demo.recommendations.domain;

import com.zeki.demo.workouts.domain.Workout;
import java.util.List;
import lombok.Getter;

@Getter
public class WorkoutRecommendation {
    private final Workout workout;
    private final int score;
    private final List<String> explanations;

    public WorkoutRecommendation(Workout workout, int score, List<String> explanations) {
        this.workout = workout;
        this.score = score;
        this.explanations = explanations == null ? List.of() : List.copyOf(explanations);
    }

    public String getReason() {
        if (explanations.isEmpty()) {
            return "Recommended as a balanced option for your current profile.";
        }
        return "Recommended because it " + String.join(" and ", explanations) + ".";
    }
}
