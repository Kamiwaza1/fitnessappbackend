package com.zeki.demo.users.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        UserResponse user
) {
}
