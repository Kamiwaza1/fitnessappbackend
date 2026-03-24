package com.zeki.demo;

import com.zeki.demo.controller.AuthController;
import com.zeki.demo.dto.AuthRequest;
import com.zeki.demo.dto.AuthResponse;
import com.zeki.demo.dto.RegisterRequest;
import com.zeki.demo.service.Interface.IAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class AuthControllerTest {

    @Mock
    private IAuthService authService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(authService);
    }

    @Test
    void register_ValidRequest_ReturnsToken() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setFullName("Test User");
        request.setPassword("StrongPass123!");

        AuthResponse mockResponse = new AuthResponse("mockToken", null, null, null, null, null);
        Mockito.when(authService.register(any(RegisterRequest.class))).thenReturn(mockResponse);

        ResponseEntity<AuthResponse> response = authController.register(request);
        assertEquals("mockToken", response.getBody().getToken());
        Mockito.verify(authService).register(request);
    }

    @Test
    void register_InvalidEmail_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalid-email");
        request.setFullName("Test User");
        request.setPassword("StrongPass123!");

        Mockito.when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Invalid email format"));

        Exception exception = assertThrows(RuntimeException.class, () -> authController.register(request));
        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    void register_WeakPassword_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setFullName("Test User");
        request.setPassword("weak");

        Mockito.when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Password must be at least 12 characters long"));

        Exception exception = assertThrows(RuntimeException.class, () -> authController.register(request));
        assertEquals("Password must be at least 12 characters long", exception.getMessage());
    }

    @Test
    void login_ValidRequest_ReturnsToken() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@example.com");
        request.setPassword("StrongPass123!");

        AuthResponse mockResponse = new AuthResponse("mockToken", null, null, null, null, null);
        Mockito.when(authService.login(any(AuthRequest.class))).thenReturn(mockResponse);

        ResponseEntity<AuthResponse> response = authController.login(request);
        assertEquals("mockToken", response.getBody().getToken());
        Mockito.verify(authService).login(request);
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongPassword");

        Mockito.when(authService.login(any(AuthRequest.class)))
                .thenThrow(new RuntimeException("Invalid email or password"));

        Exception exception = assertThrows(RuntimeException.class, () -> authController.login(request));
        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    public void testRegisterWithoutRole_UsesDefaultUserRole() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("StrongPass123!");
        AuthResponse mockResponse = new AuthResponse("mockToken", null, null, null, null, null);
        Mockito.when(authService.register(any(RegisterRequest.class))).thenReturn(mockResponse);
        ResponseEntity<AuthResponse> response = authController.register(request);
        assertEquals("mockToken", response.getBody().getToken());
    }

   

    
}
