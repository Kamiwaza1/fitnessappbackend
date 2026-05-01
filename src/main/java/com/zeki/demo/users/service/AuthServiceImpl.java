package com.zeki.demo.users.service;

import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.common.exception.ConflictException;
import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.users.domain.UserValidator;
import com.zeki.demo.users.dto.AuthRequest;
import com.zeki.demo.users.dto.AuthResponse;
import com.zeki.demo.users.dto.RegisterRequest;
import com.zeki.demo.users.entity.UserEntity;
import com.zeki.demo.users.repository.jpa.UserRepoJPA;
import com.zeki.demo.users.security.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepoJPA userRepoJPA;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserValidator userValidator;

    @Override
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = UserValidator.normalizeEmail(request.getEmail());
        if (normalizedEmail.isBlank()) {
            throw new BadRequestException("Email is required");
        }

        List<String> passwordIssues = userValidator.getPasswordIssues(request.getPassword());
        if (!passwordIssues.isEmpty()) {
            throw new BadRequestException("Password is not strong enough: " + String.join(", ", passwordIssues));
        }

        if (userRepoJPA.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ConflictException("An account with this email already exists");
        }

        String fullName = UserValidator.resolveFullName(request.getFullName(), normalizedEmail);
        UserEntity user = UserEntity.builder()
                .fullName(fullName)
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();

        UserEntity saved = userRepoJPA.save(user);
        UserDetails userDetails = userValidator.toUserDetails(saved);
        String token = jwtService.generateToken(userDetails, saved.getRole());

        return AuthResponse.builder()
                .token(token)
                .fullName(saved.getFullName())
                .email(saved.getEmail())
                .role(saved.getRole())
                .id(saved.getId())
                .build();
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        String normalizedEmail = UserValidator.normalizeEmail(request.getEmail());
        if (normalizedEmail.isBlank()) {
            throw new BadRequestException("Email is required");
        }

        UserEntity user = userRepoJPA.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new UnauthorizedException("No account found with this email"));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException ex) {
            throw new UnauthorizedException("Invalid email or password");
        }

        UserDetails userDetails = userValidator.toUserDetails(user);
        String token = jwtService.generateToken(userDetails, user.getRole());

        return AuthResponse.builder()
                .token(token)
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .id(user.getId())
                .build();
    }
}
