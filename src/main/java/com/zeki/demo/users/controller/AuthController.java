package com.zeki.demo.users.controller;

import com.zeki.demo.users.dto.*;
import com.zeki.demo.users.mapperdtodomain.UserDtoDomain;
import com.zeki.demo.users.security.JwtCookieService;
import com.zeki.demo.users.service.AuthService;
import com.zeki.demo.users.service.AuthServiceImpl;
import com.zeki.demo.users.service.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "auth", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;
    private final UserDtoDomain userDtoDomain;
    private final JwtCookieService jwtCookieService;

    @Autowired
    public AuthController(AuthService authService, UserDtoDomain userDtoDomain, JwtCookieService jwtCookieService) {
        this.authService = authService;
        this.userDtoDomain = userDtoDomain;
        this.jwtCookieService = jwtCookieService;
    }

    public AuthController(AuthService authService, UserDtoDomain userDtoDomain) {
        this(authService, userDtoDomain, null);
    }

    @Operation(summary = "Get CSRF token", description = "Issues a CSRF token for browser or API clients")
    @GetMapping("/csrf")
    @ResponseStatus(HttpStatus.OK)
    public CsrfTokenResponse csrf(CsrfToken csrfToken) {
        return new CsrfTokenResponse(
                csrfToken.getHeaderName(),
                csrfToken.getParameterName(),
                csrfToken.getToken()
        );
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        AuthenticatedUser authenticatedUser = authService.register(userDtoDomain.toDomain(request));
        addAuthCookies(response, authenticatedUser);
        return userDtoDomain.toAuthResponse(authenticatedUser);
    }

    public AuthResponse register(RegisterRequest request) {
        AuthenticatedUser authenticatedUser = authService.register(userDtoDomain.toDomain(request));
        return userDtoDomain.toAuthResponse(authenticatedUser);
    }

    @Operation(summary = "Login user", description = "Authenticates user and returns token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AuthenticatedUser authenticatedUser = authService.login(userDtoDomain.toDomain(request));
        addAuthCookies(response, authenticatedUser);
        return userDtoDomain.toAuthResponse(authenticatedUser);
    }

    public AuthResponse login(LoginRequest request) {
        AuthenticatedUser authenticatedUser = authService.login(userDtoDomain.toDomain(request));
        return userDtoDomain.toAuthResponse(authenticatedUser);
    }

    @Operation(summary = "Refresh tokens", description = "Uses a refresh token to issue new access and refresh tokens")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tokens refreshed successfully"),
            @ApiResponse(responseCode = "400", description = "Refresh token is missing"),
            @ApiResponse(responseCode = "401", description = "Refresh token is invalid or expired")
    })
    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse refresh(
            @RequestBody(required = false) RefreshTokenRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response
    ) {
        AuthenticatedUser authenticatedUser = refreshAuthenticatedUser(resolveRefreshToken(request, httpRequest));
        addAuthCookies(response, authenticatedUser);
        return userDtoDomain.toAuthResponse(authenticatedUser);
    }

    @Operation(summary = "Request a password reset email", description = "Sends a one-time password reset link if the account exists")
    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public PasswordResetResponse forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.requestPasswordReset(request.email());
        return new PasswordResetResponse(AuthServiceImpl.PASSWORD_RESET_RESPONSE);
    }

    @Operation(summary = "Reset password", description = "Sets a new password using a valid one-time reset token")
    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    public PasswordResetResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.token(), request.code(), request.newPassword());
        return new PasswordResetResponse("Password has been reset successfully.");
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        AuthenticatedUser authenticatedUser = refreshAuthenticatedUser(request.refreshToken());
        return userDtoDomain.toAuthResponse(authenticatedUser);
    }

    @Operation(summary = "Logout user", description = "Clears authentication cookies")
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletResponse response) {
        if (jwtCookieService != null) {
            jwtCookieService.clearAuthCookies(response);
        }
    }

    private AuthenticatedUser refreshAuthenticatedUser(String refreshToken) {
        return authService.refresh(refreshToken);
    }

    private String resolveRefreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest) {
        if (request != null && request.refreshToken() != null && !request.refreshToken().isBlank()) {
            return request.refreshToken();
        }
        if (jwtCookieService == null) {
            return null;
        }
        return jwtCookieService.getRefreshToken(httpRequest).orElse(null);
    }

    private void addAuthCookies(HttpServletResponse response, AuthenticatedUser authenticatedUser) {
        if (jwtCookieService != null) {
            jwtCookieService.addAuthCookies(response, authenticatedUser);
        }
    }

    public record CsrfTokenResponse(
            String headerName,
            String parameterName,
            String token
    ) {
    }
}
