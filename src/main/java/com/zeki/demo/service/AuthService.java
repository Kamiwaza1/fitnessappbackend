package com.zeki.demo.service;

import com.zeki.demo.dto.RegisterRequest;
import com.zeki.demo.dto.AuthRequest;
import com.zeki.demo.dto.AuthResponse;
import com.zeki.demo.security.JWTService;
import com.zeki.demo.service.Interface.IAuthService;

import com.zeki.demo.repository.UserRepository;
import com.zeki.demo.entity.User;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(com.zeki.demo.dto.RegisterRequest request) {
                // Email format validation
                if (!isValidEmail(request.getEmail())) {
                        throw new RuntimeException("Invalid email format");
                }
                // Password requirements validation
                String password = request.getPassword();
                if (password == null || password.length() < 12) {
                        throw new RuntimeException("Password must be at least 12 characters long");
                }
                if (!password.chars().anyMatch(Character::isUpperCase)) {
                        throw new RuntimeException("Password must contain at least one uppercase letter");
                }
                if (!password.chars().anyMatch(Character::isDigit)) {
                        throw new RuntimeException("Password must contain at least one number");
                }
                if (!password.chars().anyMatch(c -> !Character.isLetter(c))) {
                        throw new RuntimeException("Password must contain at least one non-letter character");
                }
                // Duplicate user check
                if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                        throw new RuntimeException("User already exists with this email");
                }
                User user = User.builder()
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(password))
                                .role("USER")
                                .build();

                userRepository.save(user);

                UserDetails userDetails = org.springframework.security.core.userdetails.User
                                .withUsername(user.getEmail())
                                .password(user.getPassword())
                                .authorities("ROLE_" + user.getRole())
                                .build();

                String token = jwtService.generateToken(userDetails, user.getRole());
                return new AuthResponse(token);
    }

    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public AuthResponse login(com.zeki.demo.dto.AuthRequest request) {
        // User existence check
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

                UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities("ROLE_" + user.getRole())
                        .build();

        String token = jwtService.generateToken(userDetails, user.getRole());
        return new AuthResponse(token);
    }
}
