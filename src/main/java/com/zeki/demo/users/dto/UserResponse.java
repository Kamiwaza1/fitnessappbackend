package com.zeki.demo.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserResponse {
    private UUID id;
    private String fullName;
    private String email;
    private String role;
}
