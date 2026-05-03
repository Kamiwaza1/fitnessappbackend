package com.zeki.demo.users.controller;

import com.zeki.demo.users.dto.*;
import com.zeki.demo.users.mapperDtoDomain.UserDtoDomain;
import com.zeki.demo.users.service.AuthService;
import com.zeki.demo.users.service.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "auth", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;
    private final UserDtoDomain userDtoDomain;

    @Operation(summary = "Register a new user", description = "Creates a new user account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
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
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        AuthenticatedUser authenticatedUser = authService.login(userDtoDomain.toDomain(request));
        return userDtoDomain.toAuthResponse(authenticatedUser);
    }
}
