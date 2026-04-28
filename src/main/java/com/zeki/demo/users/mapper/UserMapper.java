package com.zeki.demo.users.mapper;

import com.zeki.demo.users.dto.UserResponse;
import com.zeki.demo.users.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toCurrentUserResponse(UserEntity user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
