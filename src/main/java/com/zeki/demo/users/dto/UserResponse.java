package com.zeki.demo.users.dto;

import com.zeki.demo.users.enums.ExperienceLevel;
import com.zeki.demo.workouts.enums.WorkoutCategory;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        Integer dailyCalorieTarget,
        WorkoutCategory fitnessGoal,
        ExperienceLevel experienceLevel,
        String role
) {
    public UserResponse(
            UUID id,
            String fullName,
            String email,
            Integer dailyCalorieTarget,
            String role
    ) {
        this(id, fullName, email, dailyCalorieTarget, null, null, role);
    }
}
