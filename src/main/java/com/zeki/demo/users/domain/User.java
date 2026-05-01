package com.zeki.demo.users.domain;

import java.util.UUID;

public class User {

    private final UUID id;
    private final String fullName;
    private final String email;
    private final String password;
    private final Role role;

    public User(UUID id, String fullName, String email, String password, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }
    public User update(String username, String email) {
        return new User(this.id, username, email, this.password, this.role);
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }
}