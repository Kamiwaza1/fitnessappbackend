package com.zeki.demo;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.zeki.demo.dto.AuthRequest;
import com.zeki.demo.dto.RegisterRequest;

@RestController
public class TestController {
    @GetMapping("/api/test")
    public String test(Authentication authentication) {
        return "Hello, " + authentication.getName();
    }
    @PostMapping("/api/register")
    public String register(@RequestBody RegisterRequest request) {
        return "Registered user: " + request.getEmail();
    }
    @PostMapping("/api/login")
    public String login(@RequestBody AuthRequest request) {
        return "Logged in user: " + request.getEmail();
    }
}