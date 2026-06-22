package com.zeki.demo.unit;

import com.zeki.demo.users.controller.AuthController;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.dto.AuthResponse;
import com.zeki.demo.users.dto.ForgotPasswordRequest;
import com.zeki.demo.users.dto.LoginRequest;
import com.zeki.demo.users.dto.PasswordResetResponse;
import com.zeki.demo.users.dto.RefreshTokenRequest;
import com.zeki.demo.users.dto.RegisterRequest;
import com.zeki.demo.users.dto.ResetPasswordRequest;
import com.zeki.demo.users.dto.UserResponse;
import com.zeki.demo.users.enums.ExperienceLevel;
import com.zeki.demo.users.mapperdtodomain.UserDtoDomain;
import com.zeki.demo.users.security.JwtCookieService;
import com.zeki.demo.users.service.AuthService;
import com.zeki.demo.users.service.AuthServiceImpl;
import com.zeki.demo.users.service.AuthenticatedUser;
import com.zeki.demo.workouts.enums.WorkoutCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthControllerRestCoverageTest {

    private AuthService authService;
    private UserDtoDomain userDtoDomain;
    private JwtCookieService jwtCookieService;
    private AuthController controller;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        userDtoDomain = mock(UserDtoDomain.class);
        jwtCookieService = mock(JwtCookieService.class);
        controller = new AuthController(authService, userDtoDomain, jwtCookieService);
    }

    @Test
    void csrfReturnsTokenMetadata() {
        CsrfToken token = mock(CsrfToken.class);
        when(token.getHeaderName()).thenReturn("X-XSRF-TOKEN");
        when(token.getParameterName()).thenReturn("_csrf");
        when(token.getToken()).thenReturn("csrf-token");

        AuthController.CsrfTokenResponse response = controller.csrf(token);

        assertEquals("X-XSRF-TOKEN", response.headerName());
        assertEquals("_csrf", response.parameterName());
        assertEquals("csrf-token", response.token());
    }

    @Test
    void registerAndLoginAddAuthCookies() {
        User user = mock(User.class);
        AuthenticatedUser authenticatedUser = authenticatedUser();
        AuthResponse authResponse = authResponse();
        RegisterRequest registerRequest = new RegisterRequest("Owner", "owner@example.com", "Password123!");
        LoginRequest loginRequest = new LoginRequest("owner@example.com", "Password123!");
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();

        when(userDtoDomain.toDomain(registerRequest)).thenReturn(user);
        when(userDtoDomain.toDomain(loginRequest)).thenReturn(user);
        when(authService.register(user)).thenReturn(authenticatedUser);
        when(authService.login(user)).thenReturn(authenticatedUser);
        when(userDtoDomain.toAuthResponse(authenticatedUser)).thenReturn(authResponse);

        assertEquals(authResponse, controller.register(registerRequest, servletResponse));
        assertEquals(authResponse, controller.login(loginRequest, servletResponse));

        verify(jwtCookieService, times(2)).addAuthCookies(servletResponse, authenticatedUser);
    }

    @Test
    void refreshUsesRequestTokenBeforeCookieToken() {
        AuthenticatedUser authenticatedUser = authenticatedUser();
        AuthResponse authResponse = authResponse();
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("request-refresh-token");

        when(authService.refresh("request-refresh-token")).thenReturn(authenticatedUser);
        when(userDtoDomain.toAuthResponse(authenticatedUser)).thenReturn(authResponse);

        assertEquals(authResponse, controller.refresh(refreshRequest, servletRequest, servletResponse));

        verify(authService).refresh("request-refresh-token");
        verify(jwtCookieService).addAuthCookies(servletResponse, authenticatedUser);
    }

    @Test
    void refreshFallsBackToCookieToken() {
        AuthenticatedUser authenticatedUser = authenticatedUser();
        AuthResponse authResponse = authResponse();
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();

        when(jwtCookieService.getRefreshToken(servletRequest)).thenReturn(Optional.of("cookie-refresh-token"));
        when(authService.refresh("cookie-refresh-token")).thenReturn(authenticatedUser);
        when(userDtoDomain.toAuthResponse(authenticatedUser)).thenReturn(authResponse);

        assertEquals(authResponse, controller.refresh(null, servletRequest, servletResponse));

        verify(authService).refresh("cookie-refresh-token");
        verify(jwtCookieService).addAuthCookies(servletResponse, authenticatedUser);
    }

    @Test
    void logoutAndPasswordResetEndpointsDelegate() {
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        ForgotPasswordRequest forgotRequest = new ForgotPasswordRequest("owner@example.com");
        ResetPasswordRequest resetRequest = new ResetPasswordRequest("token", "123456", "NewPassword123!");

        controller.logout(servletResponse);
        PasswordResetResponse forgotResponse = controller.forgotPassword(forgotRequest);
        PasswordResetResponse resetResponse = controller.resetPassword(resetRequest);

        verify(jwtCookieService).clearAuthCookies(servletResponse);
        verify(authService).requestPasswordReset("owner@example.com");
        verify(authService).resetPassword("token", "123456", "NewPassword123!");
        assertEquals(AuthServiceImpl.PASSWORD_RESET_RESPONSE, forgotResponse.message());
        assertEquals("Password has been reset successfully.", resetResponse.message());
    }

    private AuthenticatedUser authenticatedUser() {
        return mock(AuthenticatedUser.class);
    }

    private AuthResponse authResponse() {
        return new AuthResponse(new UserResponse(
                UUID.randomUUID(),
                "Owner",
                "owner@example.com",
                2200,
                WorkoutCategory.GENERAL,
                ExperienceLevel.BEGINNER,
                "USER"
        ));
    }
}
