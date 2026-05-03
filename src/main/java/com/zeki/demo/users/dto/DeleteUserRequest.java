package com.zeki.demo.users.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteUserRequest {
    private UUID userId;
    public DeleteUserRequest(UUID userId) {
        this.userId = userId;
    }
}
