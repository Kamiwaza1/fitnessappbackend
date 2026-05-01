package com.zeki.demo.workouts.controller;

import com.zeki.demo.workouts.dto.CreateWorkoutRequest;
import com.zeki.demo.workouts.dto.UpdateWorkoutRequest;
import com.zeki.demo.workouts.dto.WorkoutResponse;
import com.zeki.demo.workouts.mapperDtoDomain.WorkoutDtoDomain;
import com.zeki.demo.workouts.service.WorkoutService;
import com.zeki.demo.workouts.domain.Workout;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workouts")
@RequiredArgsConstructor
@Tag(name = "Workouts", description = "Endpoints for managing workouts")
public class WorkoutController {

    private final WorkoutService workoutService;
    private final WorkoutDtoDomain workoutDtoDomain;

    @Operation(summary = "Get all workouts for the current user")
    @ApiResponse(responseCode = "200", description = "List of workouts for the current user")
    @GetMapping
    public List<WorkoutResponse> getMyWorkouts(Authentication authentication) {
        return workoutService.getMyWorkouts(authentication.getName())
                .stream()
                .map(workoutDtoDomain::toResponse)
                .toList();
    }

    @Operation(summary = "Get a workout by ID for the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Workout details for the current user"),
            @ApiResponse(responseCode = "404", description = "Workout not found")
    })
    @GetMapping("/{workoutId}")
    public WorkoutResponse getWorkoutById(@PathVariable UUID workoutId) {
        return workoutDtoDomain.toResponse(workoutService.getWorkoutById(workoutId));
    }

    @Operation(summary = "Create a new workout for the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Workout created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Workout already exists for the given date")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkoutResponse createWorkout(@Valid @RequestBody  CreateWorkoutRequest createWorkoutRequest, Authentication authentication) {
        Workout workout = workoutDtoDomain.toDomain(createWorkoutRequest, authentication.getName());
        return workoutDtoDomain.toResponse(workoutService.createWorkout(workout));
    }

    @Operation(summary = "Update an existing workout for the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Workout updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Workout not found")
    })
    @PatchMapping("/{workoutId}")
    public WorkoutResponse updateWorkout(@PathVariable UUID workoutId,
                                 @Valid @RequestBody UpdateWorkoutRequest updateWorkoutRequest) {
        Workout workout = workoutService.updateWorkout(workoutId, updateWorkoutRequest);
        return workoutDtoDomain.toResponse(workout);
    }

    @Operation(summary = "Delete an existing workout for the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Workout deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Workout not found")
    })
    @DeleteMapping("/{workoutId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWorkout(@PathVariable UUID workoutId) {
        workoutService.deleteWorkout(workoutId);
    }
}
