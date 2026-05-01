package com.zeki.demo.users.service;

import com.zeki.demo.users.dto.AuthRequest;
import com.zeki.demo.users.dto.AuthResponse;
import com.zeki.demo.users.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(AuthRequest request);
}
