package com.zeki.demo.users.service;

import com.zeki.demo.users.dto.UpdateUserRequest;
import com.zeki.demo.users.dto.UserResponse;

public interface UserService {
    UserResponse getCurrentUser(String email);
    UserResponse updateUser(String email, UpdateUserRequest request);
    void deleteCurrentUser(String email);
}
