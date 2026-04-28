package com.zeki.demo.users.service;

import com.zeki.demo.auth.dto.RegisterRequest;
import com.zeki.demo.users.dto.UserResponse;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserResponse registerUser(RegisterRequest request);

    Optional<UserResponse> getUserById(UUID id);
}
