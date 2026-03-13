package com.zeki.demo;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/api/test")
    public String test(Authentication authentication) {
        return "Hello, " + authentication.getName();
    }
    @GetMapping("/api/admin/adminDashboard")
    public String adminDashboard(Authentication authentication) {
        return "Welcome admin";
    }
}