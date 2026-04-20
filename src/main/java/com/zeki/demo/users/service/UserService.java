package com.zeki.demo.users.service;


import com.zeki.demo.users.domain.UserDomainService;
import com.zeki.demo.users.dto.CurrentUserResponse;
import com.zeki.demo.auth.dto.RegisterRequest;
import com.zeki.demo.users.entity.User;
import com.zeki.demo.users.mapper.UserMapper;
import com.zeki.demo.users.repository.UserRepository;
import com.zeki.demo.users.service.interfaces.IUserService;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

@Service

public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDomainService userDomainService;
    private final UserMapper userMapper;

 
    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserDomainService userDomainService,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDomainService = userDomainService;
        this.userMapper = userMapper;
    }

    @Override
    public CurrentUserResponse registerUser(RegisterRequest request) {
        String normalizedEmail = userDomainService.normalizeEmail(request.getEmail());
        String fullName = userDomainService.resolveFullName(request.getFullName(), normalizedEmail);
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = userDomainService.buildRegisteredUser(fullName, normalizedEmail, encodedPassword);

        return userMapper.toCurrentUserResponse(userRepository.save(user));
    }

    @Override
    public Optional<CurrentUserResponse> getUserById(UUID id) {
        return userRepository.findById(id).map(userMapper::toCurrentUserResponse);
    }
    

}
