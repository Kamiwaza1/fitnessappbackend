package com.zeki.demo.recommendations.controller;

import com.zeki.demo.recommendations.dto.WorkoutRecommendationResponse;
import com.zeki.demo.recommendations.mapperdtodomain.RecommendationDtoDomain;
import com.zeki.demo.recommendations.service.RecommendationService;
import com.zeki.demo.workouts.dto.WorkoutResponse;
import com.zeki.demo.workouts.mapperdtodomain.WorkoutDtoDomain;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recommendations", description = "Workout recommendation endpoints")
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationDtoDomain recommendationDtoDomain;
    private final WorkoutDtoDomain workoutDtoDomain;

    @Operation(summary = "Get workout recommendations for the current user")
    @GetMapping
    public List<WorkoutRecommendationResponse> getRecommendations(Authentication authentication) {
        return recommendationService.getRecommendations(authentication.getName())
                .stream()
                .map(recommendationDtoDomain::toResponse)
                .toList();
    }

    @Operation(summary = "Ignore a workout recommendation")
    @PostMapping("/{workoutId}/ignore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void ignoreRecommendation(@PathVariable UUID workoutId, Authentication authentication) {
        recommendationService.ignoreRecommendation(workoutId, authentication.getName());
    }

    @Operation(summary = "Start a recommended workout")
    @PostMapping("/{workoutId}/start")
    public WorkoutResponse startRecommendation(@PathVariable UUID workoutId, Authentication authentication) {
        return workoutDtoDomain.toResponse(recommendationService.startRecommendation(workoutId, authentication.getName()));
    }
}
