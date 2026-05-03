package com.zeki.demo.users.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UserResponse user
) {
}
