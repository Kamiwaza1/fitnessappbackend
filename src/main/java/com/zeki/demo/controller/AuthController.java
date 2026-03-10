package com.zeki.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.zeki.demo.service.AuthService;
import com.zeki.demo.dto.AuthRequest;
import com.zeki.demo.dto.AuthResponse;
import com.zeki.demo.dto.RegisterRequest;
import org.apache.catalina.connector.Response;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<com.zeki.demo.dto.AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    @PostMapping("/login")
    public ResponseEntity<com.zeki.demo.dto.AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }   
}