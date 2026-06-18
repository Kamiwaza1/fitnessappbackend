package com.zeki.demo.recommendations.service;

import com.zeki.demo.common.exception.NotFoundException;
import com.zeki.demo.recommendations.domain.RecommendationFeedback;
import com.zeki.demo.recommendations.domain.WorkoutRecommendation;
import com.zeki.demo.recommendations.repository.RecommendationFeedbackRepository;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.service.UserService;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.repository.WorkoutRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final UserService userService;
    private final WorkoutRepository workoutRepository;
    private final RecommendationFeedbackRepository feedbackRepository;
    private final RecommendationEngine recommendationEngine;

    @Override
    public List<WorkoutRecommendation> getRecommendations(String userEmail) {
        return recommendationEngine.recommend(userService.getCurrentUser(userEmail));
    }

    @Override
    public void ignoreRecommendation(UUID workoutId, String userEmail) {
        User user = userService.getCurrentUser(userEmail);
        workoutRepository.getWorkoutById(workoutId)
                .orElseThrow(() -> new NotFoundException("Workout recommendation not found"));
        LocalDateTime now = LocalDateTime.now();
        RecommendationFeedback feedback = feedbackRepository.findByUserIdAndWorkoutId(user.getUserId(), workoutId)
                .map(existing -> new RecommendationFeedback(
                        existing.getId(),
                        existing.getUserId(),
                        existing.getWorkoutId(),
                        true,
                        existing.getCreatedAt(),
                        now
                ))
                .orElse(new RecommendationFeedback(null, user.getUserId(), workoutId, true, now, now));
        feedbackRepository.save(feedback);
    }

    @Override
    public Workout startRecommendation(UUID workoutId, String userEmail) {
        Workout recommendedWorkout = workoutRepository.getWorkoutById(workoutId)
                .orElseThrow(() -> new NotFoundException("Workout recommendation not found"));
        if (recommendedWorkout.getUserEmail().equalsIgnoreCase(userEmail)) {
            return recommendedWorkout;
        }
        Workout copy = new Workout(
                recommendedWorkout.getWorkoutName(),
                LocalDate.now(),
                recommendedWorkout.getDurationMinutes(),
                recommendedWorkout.getSets(),
                recommendedWorkout.getReps(),
                recommendedWorkout.getWeightKg(),
                recommendedWorkout.getNotes(),
                null,
                recommendedWorkout.getDescription(),
                recommendedWorkout.getCategory(),
                recommendedWorkout.getMuscleType(),
                recommendedWorkout.getDifficulty(),
                recommendedWorkout.getExercises(),
                LocalDate.now(),
                LocalDate.now(),
                userEmail
        );
        copy.validate();
        return workoutRepository.save(copy);
    }
}
