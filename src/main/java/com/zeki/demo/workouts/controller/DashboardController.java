package com.zeki.demo.workouts.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zeki.demo.workouts.service.WorkoutService;

@RestController
@RequestMapping("/api/workouts")
public class DashboardController {

    private final WorkoutService workoutService;

    public DashboardController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @GetMapping("/summary")
    public ResponseEntity<String> getDashboard(Authentication authentication) {
        String dashboardData = workoutService.getDashboardData(authentication.getName());
        return ResponseEntity.ok(dashboardData);
    }
}
