package com.zeki.demo.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String fullName;
    private String email;
    private String role;
    private Long id;
}
