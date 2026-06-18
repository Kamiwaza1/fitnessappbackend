package com.zeki.demo.users.mapperdtodomain;

import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.dto.AuthResponse;
import com.zeki.demo.users.dto.LoginRequest;
import com.zeki.demo.users.dto.RegisterRequest;
import com.zeki.demo.users.dto.UpdateUserRequest;
import com.zeki.demo.users.dto.UserResponse;
import com.zeki.demo.users.service.AuthenticatedUser;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class UserDtoDomain {

    public User toDomain(RegisterRequest request) {
        LocalDateTime now = LocalDateTime.now();
        return new User(
                null,
                request.fullName(),
                request.email(),
                request.password(),
                null,
                "USER",
                true,
                now,
                now
        );
    }

    public User toDomain(LoginRequest request) {
        return new User(
                null,
                null,
                request.email(),
                request.password(),
                null,
                "USER",
                true,
                null,
                null
        );
    }

    public User toUpdatedDomain(UpdateUserRequest request, User existingUser, String encodedPassword) {
        return new User(
                existingUser.getUserId(),
                request.fullName() != null ? request.fullName() : existingUser.getFullName(),
                request.email() != null ? request.email() : existingUser.getEmail(),
                encodedPassword,
                request.dailyCalorieTarget() != null ? request.dailyCalorieTarget() : existingUser.getDailyCalorieTarget(),
                request.fitnessGoal() != null ? request.fitnessGoal() : existingUser.getFitnessGoal(),
                request.experienceLevel() != null ? request.experienceLevel() : existingUser.getExperienceLevel(),
                existingUser.getRole(),
                existingUser.isActive(),
                existingUser.getCreatedAt(),
                LocalDateTime.now(),
                existingUser.getDeletionRequestedAt(),
                existingUser.getDeletionScheduledFor()
        );
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                User.resolveFullName(user.getFullName(), user.getEmail()),
                user.getEmail(),
                user.getDailyCalorieTarget(),
                user.getFitnessGoal(),
                user.getExperienceLevel(),
                user.getRole()
        );
    }

    public AuthResponse toAuthResponse(AuthenticatedUser authenticatedUser) {
        return new AuthResponse(
                toResponse(authenticatedUser.user())
        );
    }
}
