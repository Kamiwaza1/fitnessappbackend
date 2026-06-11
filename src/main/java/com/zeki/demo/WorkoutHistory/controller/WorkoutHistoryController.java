package com.zeki.demo.WorkoutHistory.controller;

import com.zeki.demo.WorkoutHistory.dto.CreateWorkoutHistoryRequest;
import com.zeki.demo.WorkoutHistory.dto.WorkoutHistoryResponse;
import com.zeki.demo.WorkoutHistory.mapperDtoDomain.WorkoutHistoryDtoDomain;
import com.zeki.demo.WorkoutHistory.service.WorkoutHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1/workout-history", "/api/v1/workout-histories"})
@RequiredArgsConstructor
@Tag(name = "Workout History", description = "Endpoints for viewing workout history")
public class WorkoutHistoryController {

    private final WorkoutHistoryService workoutHistoryService;
    private final WorkoutHistoryDtoDomain workoutHistoryDtoDomain;

    @Operation(summary = "Create a workout history record for the current user")
    @ApiResponse(responseCode = "201", description = "Workout history record created")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkoutHistoryResponse createWorkoutHistory(
            @Valid @RequestBody CreateWorkoutHistoryRequest request,
            Authentication authentication
    ) {
        return workoutHistoryDtoDomain.toResponse(
                workoutHistoryService.createWorkoutHistory(request, authentication.getName())
        );
    }

    @Operation(summary = "Get all workout history records for the current user")
    @ApiResponse(responseCode = "200", description = "List of workout history records")
    @GetMapping
    public List<WorkoutHistoryResponse> getWorkoutHistory(Authentication authentication) {
        return workoutHistoryService.getWorkoutHistory(authentication.getName())
                .stream()
                .map(workoutHistoryDtoDomain::toResponse)
                .toList();
    }

    @Operation(summary = "Get a workout history record by workout ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Workout history record"),
            @ApiResponse(responseCode = "404", description = "Workout not found")
    })
    @GetMapping("/{workoutId}")
    public WorkoutHistoryResponse getWorkoutHistoryById(
            @PathVariable UUID workoutId,
            Authentication authentication
    ) {
        return workoutHistoryDtoDomain.toResponse(
                workoutHistoryService.getWorkoutHistoryById(workoutId, authentication.getName())
        );
    }
}
