package com.zeki.demo.users.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        Integer dailyCalorieTarget,
        String role
) {
}
