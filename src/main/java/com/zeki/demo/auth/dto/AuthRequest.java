package com.zeki.demo.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequest {
    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Email is required")
    @Size(max = 254, message = "Email must be at most 254 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(max = 128, message = "Password must be at most 128 characters")
    private String password;
}
