package com.zeki.demo.workouts.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zeki.demo.workouts.service.interfaces.IWorkoutService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final IWorkoutService workoutService;

    public DashboardController(IWorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @GetMapping
    public ResponseEntity<String> getDashboard(Authentication authentication) {
        String dashboardData = workoutService.getDashboardData(authentication.getName());
        return ResponseEntity.ok(dashboardData);
    }
}
