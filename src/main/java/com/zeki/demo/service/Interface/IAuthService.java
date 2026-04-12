package com.zeki.demo.service.Interface;
import com.zeki.demo.dto.AuthRequest;
import com.zeki.demo.dto.AuthResponse;
import com.zeki.demo.dto.CurrentUserResponse;
import com.zeki.demo.dto.RegisterRequest;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(AuthRequest request);
    CurrentUserResponse getCurrentUser(String email);
}
