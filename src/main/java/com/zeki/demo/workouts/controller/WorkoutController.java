package com.zeki.demo.workouts.controller;

import com.zeki.demo.workouts.dto.CreateWorkoutRequest;
import com.zeki.demo.workouts.dto.UpdateWorkoutRequest;
import com.zeki.demo.workouts.dto.WorkoutResponse;
import com.zeki.demo.workouts.service.interfaces.IWorkoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
@Tag(name = "Workouts", description = "CRUD endpoints for user workouts")
public class WorkoutController {

    private final IWorkoutService workoutService;

    @Operation(summary = "Add workout", description = "Creates a new workout for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Workout created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid workout input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<WorkoutResponse> createWorkout(
            @Valid @RequestBody CreateWorkoutRequest request,
            Authentication authentication
    ) {
        WorkoutResponse response = workoutService.createWorkout(authentication.getName(), request);
        URI location = URI.create("/api/workouts/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<WorkoutResponse>> getWorkouts(Authentication authentication) {
        List<WorkoutResponse> response = workoutService.getWorkouts(authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponse> getWorkoutById(@PathVariable Long id, Authentication authentication) {
        WorkoutResponse response = workoutService.getWorkoutById(authentication.getName(), id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutResponse> updateWorkout(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWorkoutRequest request,
            Authentication authentication
    ) {
        WorkoutResponse response = workoutService.updateWorkout(authentication.getName(), id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id, Authentication authentication) {
        workoutService.deleteWorkout(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }

    
}
