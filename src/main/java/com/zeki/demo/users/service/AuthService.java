package com.zeki.demo.users.service;

import com.zeki.demo.users.domain.User;

public interface AuthService {
    AuthenticatedUser register(User user);

    AuthenticatedUser login(User user);
}
