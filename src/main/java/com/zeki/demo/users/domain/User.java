package com.zeki.demo.users.domain;

public class User {
    private UUID userId;
    private String fullName;
    private String email;
    private String password;
    private String role;

    public User(UUID userId, String fullName, String email, String password, String role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
