package com.zeki.demo.users.mapperDtoDomain;

import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.domain.UserValidator;
import com.zeki.demo.users.dto.RegisterRequest;
import com.zeki.demo.users.dto.UserResponse;
import com.zeki.demo.users.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserDtoDomain {
    private final RegisterRequest registerRequest;

    public User toDomain(RegisterRequest registerRequest, String encodedPassword) {
        LocalDate now = LocalDate.now();

        return new User(
                registerRequest.getFullName(),
                registerRequest.getEmail(),
                encodedPassword,
                "USER",
                now
        )
    }


}
