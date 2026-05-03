package com.zeki.demo.service;

import com.zeki.demo.common.exception.ConflictException;
import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.repository.UserRepository;
import com.zeki.demo.users.security.JWTService;
import com.zeki.demo.users.service.AuthenticatedUser;
import com.zeki.demo.users.service.AuthServiceImpl;
import java.time.LocalDateTime;
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
    private UserRepository userRepository;

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
                passwordEncoder,
                jwtService,
                authenticationManager,
                userRepository
        );
    }

    @Test
    void registerWhenEmailAlreadyExistsIgnoringCaseThrowsConflict() {
        User user = new User(
                null,
                null,
                "User@Example.com",
                "StrongPassword123!",
                "USER",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class, () -> authService.register(user));
        assertEquals("An account with this email already exists", ex.getMessage());
    }

    @Test
    void loginWhenEmailDoesNotExistThrowsUnauthorized() {
        User user = new User(
                null,
                null,
                "missing@example.com",
                "StrongPassword123!",
                "USER",
                true,
                null,
                null
        );

        when(userRepository.findActiveByEmail("missing@example.com")).thenReturn(Optional.empty());

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> authService.login(user));
        assertEquals("No account found with this email", ex.getMessage());
    }

    @Test
    void loginWhenPasswordIsWrongThrowsUnauthorized() {
        User loginUser = new User(null, null, "test@example.com", "wrong-password", "USER", true, null, null);
        User storedUser = new User(
                UUID.randomUUID(),
                "Test User",
                "test@example.com",
                "encoded-password",
                "USER",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> authService.login(loginUser));
        assertEquals("Invalid email or password", ex.getMessage());
    }

    @Test
    void loginWhenCredentialsAreValidReturnsTokenAndUserInfo() {
        User loginUser = new User(null, null, "TEST@Example.com", "StrongPassword123!", "USER", true, null, null);
        User storedUser = new User(
                UUID.randomUUID(),
                "Test User",
                "test@example.com",
                "encoded-password",
                "USER",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userRepository.findActiveByEmail("test@example.com")).thenReturn(Optional.of(storedUser));
        when(jwtService.generateToken(any(UserDetails.class), eq("USER"))).thenReturn("jwt-token");

        AuthenticatedUser response = authService.login(loginUser);

        assertEquals("jwt-token", response.accessToken());
        assertEquals("test@example.com", response.user().getEmail());
        verify(authenticationManager).authenticate(
                argThat(token -> "test@example.com".equals(token.getPrincipal()))
        );
    }
}
