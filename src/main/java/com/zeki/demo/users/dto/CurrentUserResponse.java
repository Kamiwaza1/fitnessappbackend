package com.zeki.demo.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrentUserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String role;
}
