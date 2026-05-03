package com.zeki.demo.users.service;

import com.zeki.demo.users.domain.User;

public record AuthenticatedUser(
        User user,
        String accessToken,
        String refreshToken
) {
}
