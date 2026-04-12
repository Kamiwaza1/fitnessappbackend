package com.zeki.demo.auth.service.interfaces;
import com.zeki.demo.auth.dto.AuthRequest;
import com.zeki.demo.auth.dto.AuthResponse;
import com.zeki.demo.users.dto.CurrentUserResponse;
import com.zeki.demo.auth.dto.RegisterRequest;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(AuthRequest request);
    CurrentUserResponse getCurrentUser(String email);
}
