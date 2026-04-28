package com.zeki.demo.auth.service.interfaces;
import com.zeki.demo.auth.dto.AuthRequest;
import com.zeki.demo.auth.dto.AuthResponse;
import com.zeki.demo.users.dto.UserResponse;
import com.zeki.demo.auth.dto.RegisterRequest;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(AuthRequest request);
    UserResponse getCurrentUser(String email);
}
