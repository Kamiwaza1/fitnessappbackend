package com.zeki.demo.auth.service;

import com.zeki.demo.auth.domain.Auth;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.auth.dto.AuthResponse;
import com.zeki.demo.users.dto.UserResponse;
import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.common.exception.ConflictException;
import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.auth.mapper.AuthMapper;
import com.zeki.demo.users.mapper.UserMapper;
import com.zeki.demo.users.entity.UserEntity;
import com.zeki.demo.users.repository.UserRepository;
import com.zeki.demo.auth.security.JWTService;
import com.zeki.demo.auth.service.interfaces.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final Auth auth;
    private final User user;
    private final AuthMapper authMapper;
    private final UserMapper userMapper;

    @Override
    public AuthResponse register(com.zeki.demo.auth.dto.RegisterRequest request) {
        String normalizedEmail = user.normalizeEmail(request.getEmail());
        if (normalizedEmail.isBlank()) {
            throw new BadRequestException("Email is required");
        }

        String password = request.getPassword();
        List<String> passwordIssues = auth.getPasswordIssues(password);
        if (!passwordIssues.isEmpty()) {
            throw new BadRequestException(
                    "Password is not strong enough: " + String.join(", ", passwordIssues)
            );
        }

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ConflictException("An account with this email already exists");
        }

        String fullName = user.resolveFullName(request.getFullName(), normalizedEmail);

        UserEntity user = this.user.buildRegisteredUser(
                fullName,
                normalizedEmail,
                passwordEncoder.encode(password)
        );

        userRepository.save(user);

        String token = jwtService.generateToken(auth.toUserDetails(user), user.getRole());
        return authMapper.toAuthResponse(user, token);
    }

    @Override
    public AuthResponse login(com.zeki.demo.auth.dto.AuthRequest request) {
        String normalizedEmail = user.normalizeEmail(request.getEmail());
        if (normalizedEmail.isBlank()) {
            throw new BadRequestException("Email is required");
        }

        Optional<UserEntity> userOptional = userRepository.findByEmailIgnoreCase(normalizedEmail);
        if (userOptional.isEmpty()) {
            throw new UnauthorizedException("No account found with this email");
        }
        UserEntity user = userOptional.get();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtService.generateToken(auth.toUserDetails(user), user.getRole());
        return authMapper.toAuthResponse(user, token);
    }

    @Override
    public UserResponse getCurrentUser(String email) {
        String normalizedEmail = user.normalizeEmail(email);
        UserEntity user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        return userMapper.toCurrentUserResponse(user);
    }
}
