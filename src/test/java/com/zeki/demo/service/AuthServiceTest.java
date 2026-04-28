package com.zeki.demo.service;

import com.zeki.demo.auth.domain.Auth;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.auth.dto.AuthRequest;
import com.zeki.demo.auth.dto.AuthResponse;
import com.zeki.demo.auth.dto.RegisterRequest;
import com.zeki.demo.common.exception.ConflictException;
import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.auth.mapper.AuthMapper;
import com.zeki.demo.auth.service.AuthService;
import com.zeki.demo.users.mapper.UserMapper;
import com.zeki.demo.users.repository.UserRepository;
import com.zeki.demo.auth.security.JWTService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Spy
    private Auth auth = new Auth();

    @Spy
    private User user = new User();

    @Spy
    private AuthMapper authMapper = new AuthMapper();

    @Spy
    private UserMapper userMapper = new UserMapper();

    @InjectMocks
    private AuthService authService;

    @Test
    void register_whenEmailAlreadyExistsIgnoringCase_throwsConflict() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("User@Example.com");
        request.setPassword("StrongPassword123!");

        when(userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class, () -> authService.register(request));
        assertEquals("An account with this email already exists", ex.getMessage());
    }

    @Test
    void login_whenEmailDoesNotExist_throwsUnauthorized() {
        AuthRequest request = new AuthRequest();
        request.setEmail("missing@example.com");
        request.setPassword("StrongPassword123!");

        when(userRepository.findByEmailIgnoreCase("missing@example.com")).thenReturn(Optional.empty());

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> authService.login(request));
        assertEquals("No account found with this email", ex.getMessage());
    }

    @Test
    void login_whenPasswordIsWrong_throwsUnauthorized() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrong-password");

        com.zeki.demo.users.entity.UserEntity user = com.zeki.demo.users.entity.UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName("Test User")
                .email("test@example.com")
                .password("encoded-password")
                .role("USER")
                .build();

        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> authService.login(request));
        assertEquals("Invalid email or password", ex.getMessage());
    }

    @Test
    void login_whenCredentialsAreValid_returnsTokenAndUserInfo() {
        AuthRequest request = new AuthRequest();
        request.setEmail("TEST@Example.com");
        request.setPassword("StrongPassword123!");

        com.zeki.demo.users.entity.UserEntity user = com.zeki.demo.users.entity.UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName("Test User")
                .email("test@example.com")
                .password("encoded-password")
                .role("USER")
                .build();

        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(jwtService.generateToken(any(UserDetails.class), eq("USER"))).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("test@example.com", response.getEmail());
        verify(authenticationManager).authenticate(
                argThat(token -> "test@example.com".equals(token.getPrincipal()))
        );
    }
}
