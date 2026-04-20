package com.zeki.demo.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CurrentUserResponse {
    private UUID id;
    private String fullName;
    private String email;
    private String role;
}
