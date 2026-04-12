package com.zeki.demo.users.service;


import com.zeki.demo.users.domain.UserDomainService;
import com.zeki.demo.users.dto.CurrentUserResponse;
import com.zeki.demo.auth.dto.RegisterRequest;
import com.zeki.demo.users.entity.User;
import com.zeki.demo.users.mapper.UserMapper;
import com.zeki.demo.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service

public class UserService{
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

    public CurrentUserResponse registerUser(RegisterRequest request) {
        String normalizedEmail = userDomainService.normalizeEmail(request.getEmail());
        String fullName = userDomainService.resolveFullName(request.getFullName(), normalizedEmail);
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = userDomainService.buildRegisteredUser(fullName, normalizedEmail, encodedPassword);

        return userMapper.toCurrentUserResponse(userRepository.save(user));
    }

    public Optional<CurrentUserResponse> getUserById(Long id) {
        return userRepository.findById(id).map(userMapper::toCurrentUserResponse);
    }
    

}
