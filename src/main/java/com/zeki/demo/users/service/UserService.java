package com.zeki.demo.users.service;

import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.dto.UpdateUserRequest;

public interface UserService {
    User getCurrentUser(String email);

    User updateUser(String email, UpdateUserRequest updateUserRequest);

    void deleteCurrentUser(String email);
}
