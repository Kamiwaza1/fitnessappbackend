package com.zeki.demo.service;

import com.zeki.demo.common.exception.ConflictException;
import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.users.domain.UserValidator;
import com.zeki.demo.users.dto.AuthRequest;
import com.zeki.demo.users.dto.AuthResponse;
import com.zeki.demo.users.dto.RegisterRequest;
import com.zeki.demo.users.entity.UserEntity;
import com.zeki.demo.users.repository.jpa.UserRepoJPA;
import com.zeki.demo.users.security.JWTService;
import com.zeki.demo.users.service.AuthServiceImpl;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private UserRepoJPA userRepoJPA;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                userRepoJPA,
                passwordEncoder,
                jwtService,
                authenticationManager,
                new UserValidator()
        );
    }

    @Test
    void registerWhenEmailAlreadyExistsIgnoringCaseThrowsConflict() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("User@Example.com");
        request.setPassword("StrongPassword123!");

        when(userRepoJPA.existsByEmailIgnoreCase("user@example.com")).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class, () -> authService.register(request));
        assertEquals("An account with this email already exists", ex.getMessage());
    }

    @Test
    void loginWhenEmailDoesNotExistThrowsUnauthorized() {
        AuthRequest request = new AuthRequest();
        request.setEmail("missing@example.com");
        request.setPassword("StrongPassword123!");

        when(userRepoJPA.findByEmailIgnoreCaseAndIsActiveTrue("missing@example.com")).thenReturn(Optional.empty());

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> authService.login(request));
        assertEquals("No account found with this email", ex.getMessage());
    }

    @Test
    void loginWhenPasswordIsWrongThrowsUnauthorized() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrong-password");

        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName("Test User")
                .email("test@example.com")
                .password("encoded-password")
                .role("USER")
                .isActive(true)
                .build();

        when(userRepoJPA.findByEmailIgnoreCaseAndIsActiveTrue("test@example.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> authService.login(request));
        assertEquals("Invalid email or password", ex.getMessage());
    }

    @Test
    void loginWhenCredentialsAreValidReturnsTokenAndUserInfo() {
        AuthRequest request = new AuthRequest();
        request.setEmail("TEST@Example.com");
        request.setPassword("StrongPassword123!");

        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName("Test User")
                .email("test@example.com")
                .password("encoded-password")
                .role("USER")
                .isActive(true)
                .build();

        when(userRepoJPA.findByEmailIgnoreCaseAndIsActiveTrue("test@example.com")).thenReturn(Optional.of(user));
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
