package com.zeki.demo.users.service.interfaces;

import com.zeki.demo.auth.dto.RegisterRequest;
import com.zeki.demo.users.dto.CurrentUserResponse;

import java.util.Optional;
import java.util.UUID;

public interface IUserService {
    CurrentUserResponse registerUser(RegisterRequest request);
    Optional<CurrentUserResponse> getUserById(UUID id);
}
