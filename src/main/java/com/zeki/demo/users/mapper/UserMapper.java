package com.zeki.demo.users.mapper;

import com.zeki.demo.users.dto.CurrentUserResponse;
import com.zeki.demo.users.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public CurrentUserResponse toCurrentUserResponse(User user) {
        return new CurrentUserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
