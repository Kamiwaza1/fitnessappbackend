package com.zeki.demo.users.service;


import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.dto.UserResponse;
import com.zeki.demo.users.entity.UserEntity;
import com.zeki.demo.auth.dto.RegisterRequest;
import com.zeki.demo.users.mapper.UserMapper;
import com.zeki.demo.users.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

@Service

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final User user;
    private final UserMapper userMapper;

 
    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            User user,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.user = user;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse registerUser(RegisterRequest request) {
        String normalizedEmail = user.normalizeEmail(request.getEmail());
        String fullName = user.resolveFullName(request.getFullName(), normalizedEmail);
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        UserEntity user = this.user.buildRegisteredUser(fullName, normalizedEmail, encodedPassword);

        return userMapper.toCurrentUserResponse(userRepository.save(user));
    }

    @Override
    public Optional<UserResponse> getUserById(UUID id) {
        return userRepository.findById(id).map(userMapper::toCurrentUserResponse);
    }
    

}
