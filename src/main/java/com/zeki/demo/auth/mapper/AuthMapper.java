package com.zeki.demo.auth.mapper;

import com.zeki.demo.auth.dto.AuthResponse;
import com.zeki.demo.users.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public AuthResponse toAuthResponse(UserEntity user, String token) {
        return new AuthResponse(token, user.getFullName(), user.getEmail(), user.getRole(), user.getId());
    }
}
