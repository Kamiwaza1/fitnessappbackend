package com.zeki.demo.users.service;

import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.dto.AuthResponse;

public interface AuthService {
    AuthResponse register(User user);

    AuthResponse login(User user);
}
