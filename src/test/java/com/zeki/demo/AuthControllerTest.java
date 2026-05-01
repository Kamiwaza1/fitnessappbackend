package com.zeki.demo;

import com.zeki.demo.users.controller.AuthController;
import com.zeki.demo.users.dto.AuthRequest;
import com.zeki.demo.users.dto.AuthResponse;
import com.zeki.demo.users.dto.RegisterRequest;
import com.zeki.demo.users.service.AuthService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(authService);
    }

    @Test
    void registerReturnsAuthResponse() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setFullName("Test User");
        request.setPassword("StrongPass123!");

        AuthResponse mockResponse = new AuthResponse("mockToken", "Test User", "test@example.com", "USER", UUID.randomUUID());
        Mockito.when(authService.register(any(RegisterRequest.class))).thenReturn(mockResponse);

        AuthResponse response = authController.register(request);

        assertEquals("mockToken", response.getToken());
        assertEquals("test@example.com", response.getEmail());
        Mockito.verify(authService).register(request);
    }

    @Test
    void registerPropagatesServiceErrors() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalid-email");
        request.setPassword("StrongPass123!");

        Mockito.when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Invalid email format"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authController.register(request));

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    void loginReturnsTokenPayload() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@example.com");
        request.setPassword("StrongPass123!");

        AuthResponse mockResponse = new AuthResponse("mockToken", "Test User", "test@example.com", "USER", UUID.randomUUID());
        Mockito.when(authService.login(any(AuthRequest.class))).thenReturn(mockResponse);

        ResponseEntity<AuthResponse> response = authController.login(request);

        assertEquals("mockToken", response.getBody().getToken());
        Mockito.verify(authService).login(request);
    }
}
