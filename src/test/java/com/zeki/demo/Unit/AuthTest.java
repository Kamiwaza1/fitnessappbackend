package com.zeki.demo.Unit;

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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    private static final String TEST_EMAIL = "test@example.com";
    private static final String STRONG_PASSWORD = "StrongPassword123!";
    private static final String USER_ROLE = "USER";

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
        User user = userForAuth("User@Example.com", STRONG_PASSWORD);

        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class, () -> authService.register(user));
        assertEquals("An account with this email already exists", ex.getMessage());
    }

    @Test
    void registerWhenPasswordIsWeakThrowsBadRequest() {
        User user = userForAuth(TEST_EMAIL, "weak-password");

        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> authService.register(user));

        assertTrue(ex.getMessage().startsWith("Password is not strong enough:"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerWhenInputIsValidEncodesPasswordSavesUserAndReturnsToken() {
        User user = userForAuth(" Test@Example.com ", STRONG_PASSWORD);
        User savedUser = storedUser(TEST_EMAIL);

        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(STRONG_PASSWORD)).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateAccessToken(any(UserDetails.class), eq(USER_ROLE))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(TEST_EMAIL)).thenReturn("refresh-token");

        AuthenticatedUser response = authService.register(user);

        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        assertEquals(TEST_EMAIL, response.user().getEmail());
        verify(passwordEncoder).encode(STRONG_PASSWORD);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void loginWhenEmailDoesNotExistThrowsUnauthorized() {
        User user = userForAuth("missing@example.com", STRONG_PASSWORD);

        when(userRepository.findActiveByEmail("missing@example.com")).thenReturn(Optional.empty());

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> authService.login(user));
        assertEquals("No account found with this email", ex.getMessage());
    }

    @Test
    void loginWhenEmailIsBlankThrowsBadRequest() {
        User user = userForAuth("   ", STRONG_PASSWORD);

        Exception ex = assertThrows(Exception.class, () -> authService.login(user));

        assertEquals("Email is required", ex.getMessage());
    }

    @Test
    void loginWhenPasswordIsWrongThrowsUnauthorized() {
        User loginUser = userForAuth(TEST_EMAIL, "wrong-password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> authService.login(loginUser));
        assertEquals("Invalid email or password", ex.getMessage());
    }

    @Test
    void loginWhenCredentialsAreValidReturnsTokenAndUserInfo() {
        User loginUser = userForAuth("TEST@Example.com", STRONG_PASSWORD);
        User storedUser = storedUser(TEST_EMAIL);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userRepository.findActiveByEmail(TEST_EMAIL)).thenReturn(Optional.of(storedUser));
        when(jwtService.generateAccessToken(any(UserDetails.class), eq(USER_ROLE))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(TEST_EMAIL)).thenReturn("refresh-token");

        AuthenticatedUser response = authService.login(loginUser);

        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        assertEquals(TEST_EMAIL, response.user().getEmail());
        verify(authenticationManager).authenticate(
                argThat(token -> TEST_EMAIL.equals(token.getPrincipal()))
        );
    }

    @Test
    void refreshWhenTokenIsValidReturnsNewTokensAndUserInfo() {
        User storedUser = storedUser(TEST_EMAIL);

        when(jwtService.extractRefreshUsername("old-refresh-token")).thenReturn(TEST_EMAIL);
        when(userRepository.findActiveByEmail(TEST_EMAIL)).thenReturn(Optional.of(storedUser));
        when(jwtService.isRefreshTokenValid("old-refresh-token", TEST_EMAIL)).thenReturn(true);
        when(jwtService.generateAccessToken(any(UserDetails.class), eq(USER_ROLE))).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(TEST_EMAIL)).thenReturn("new-refresh-token");

        AuthenticatedUser response = authService.refresh("old-refresh-token");

        assertEquals("new-access-token", response.accessToken());
        assertEquals("new-refresh-token", response.refreshToken());
        assertEquals(TEST_EMAIL, response.user().getEmail());
    }

    @Test
    void refreshWhenTokenIsInvalidThrowsUnauthorized() {
        when(jwtService.extractRefreshUsername("bad-refresh-token")).thenReturn(TEST_EMAIL);
        when(userRepository.findActiveByEmail(TEST_EMAIL)).thenReturn(Optional.of(storedUser(TEST_EMAIL)));
        when(jwtService.isRefreshTokenValid("bad-refresh-token", TEST_EMAIL)).thenReturn(false);

        UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> authService.refresh("bad-refresh-token")
        );

        assertEquals("Invalid refresh token", ex.getMessage());
    }

    private User userForAuth(String email, String password) {
        return new User(null, null, email, password, null, USER_ROLE, true, null, null);
    }

    private User storedUser(String email) {
        LocalDateTime now = LocalDateTime.now();
        return new User(
                UUID.randomUUID(),
                "Test User",
                email,
                "encoded-password",
                null,
                USER_ROLE,
                true,
                now,
                now
        );
    }
}
