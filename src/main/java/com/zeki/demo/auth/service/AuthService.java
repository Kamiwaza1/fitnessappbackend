package com.zeki.demo.auth.service;

import com.zeki.demo.auth.domain.AuthDomainService;
import com.zeki.demo.users.domain.UserDomainService;
import com.zeki.demo.auth.dto.AuthResponse;
import com.zeki.demo.users.dto.CurrentUserResponse;
import com.zeki.demo.users.entity.User;
import com.zeki.demo.auth.mapper.AuthMapper;
import com.zeki.demo.users.mapper.UserMapper;
import com.zeki.demo.users.repository.UserRepository;
import com.zeki.demo.auth.security.JWTService;
import com.zeki.demo.auth.service.interfaces.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuthDomainService authDomainService;
    private final UserDomainService userDomainService;
    private final AuthMapper authMapper;
    private final UserMapper userMapper;

    @Override
    public AuthResponse register(com.zeki.demo.auth.dto.RegisterRequest request) {
        String normalizedEmail = userDomainService.normalizeEmail(request.getEmail());
        if (normalizedEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }

        String password = request.getPassword();
        List<String> passwordIssues = authDomainService.getPasswordIssues(password);
        if (!passwordIssues.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password is not strong enough: " + String.join(", ", passwordIssues)
            );
        }

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
        }

        String fullName = userDomainService.resolveFullName(request.getFullName(), normalizedEmail);

        User user = userDomainService.buildRegisteredUser(
                fullName,
                normalizedEmail,
                passwordEncoder.encode(password)
        );

        userRepository.save(user);

        String token = jwtService.generateToken(authDomainService.toUserDetails(user), user.getRole());
        return authMapper.toAuthResponse(user, token);
    }

    @Override
    public AuthResponse login(com.zeki.demo.auth.dto.AuthRequest request) {
        String normalizedEmail = userDomainService.normalizeEmail(request.getEmail());
        if (normalizedEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }

        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(normalizedEmail);
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No account found with this email");
        }
        User user = userOptional.get();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        String token = jwtService.generateToken(authDomainService.toUserDetails(user), user.getRole());
        return authMapper.toAuthResponse(user, token);
    }

    @Override
    public CurrentUserResponse getCurrentUser(String email) {
        String normalizedEmail = userDomainService.normalizeEmail(email);
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return userMapper.toCurrentUserResponse(user);
    }
}
