package com.zeki.demo.users.service;

import com.zeki.demo.users.dto.UpdateUserRequest;
import com.zeki.demo.users.dto.UserResponse;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserResponse getCurrentUser(String email);
    Optional<UserResponse> getUserById(UUID id);
    UserResponse updateUser(String email, UpdateUserRequest request);
}
