package com.zeki.demo.users.mapperDtoDomain;

import com.zeki.demo.users.dto.UserResponse;
import com.zeki.demo.users.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserDtoDomain {
    public UserResponse toUserResponse(UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
