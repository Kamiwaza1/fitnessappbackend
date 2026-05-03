package com.zeki.demo.users.service;

import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.common.exception.ConflictException;
import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.repository.UserRepository;
import com.zeki.demo.users.security.JWTService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Override
    public User register(User user) {
        user.validate();
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("An account with this email already exists");
        }
        List<String> passwordIssues = user.getPasswordIssues();
        if (!passwordIssues.isEmpty()) {
            throw new BadRequestException("Password is not strong enough: " + String.join(", ", passwordIssues));
        }
        user.setEncodedPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public AuthenticatedUser login(User user) {
        String normalizedEmail = User.normalizeEmail(user.getEmail());
        if (normalizedEmail.isBlank()) {
            throw new BadRequestException("Email is required");
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedEmail, user.getPassword())
            );
        } catch (AuthenticationException exception) {
            throw new UnauthorizedException("Invalid email or password");
        }
        User existingUser = userRepository.findActiveByEmail(normalizedEmail)
                .orElseThrow(() -> new UnauthorizedException("No account found with this email"));
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(existingUser.getEmail())
                .password(existingUser.getPassword())
                .authorities("ROLE_" + existingUser.getRole())
                .build();
        String accessToken = jwtService.generateToken(userDetails, existingUser.getRole());
        return new AuthenticatedUser(existingUser, accessToken, accessToken);
    }
}
