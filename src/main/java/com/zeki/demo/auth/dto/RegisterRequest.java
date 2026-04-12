package com.zeki.demo.auth.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class RegisterRequest {
    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Email is required")
    @Size(max = 254, message = "Email must be at most 254 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 12, max = 128, message = "Password must be between 12 and 128 characters")
    private String password;

    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;
}
