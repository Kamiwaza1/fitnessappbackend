package com.zeki.demo.auth.mapper;

import com.zeki.demo.auth.dto.AuthResponse;
import com.zeki.demo.users.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public AuthResponse toAuthResponse(User user, String token) {
        return new AuthResponse(token, user.getFullName(), user.getEmail(), user.getRole(), user.getId());
    }
}
