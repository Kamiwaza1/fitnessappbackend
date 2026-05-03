package com.zeki.demo.users.domain;

import com.zeki.demo.common.exception.BadRequestException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.Getter;

@Getter
public class User {
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^A-Za-z0-9]");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    private final UUID userId;
    private String fullName;
    private String email;
    private String password;
    private final String role;
    private boolean active;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(
            UUID userId,
            String fullName,
            String email,
            String password,
            String role,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.userId = userId;
        this.fullName = normalizeNullable(fullName);
        this.email = normalizeEmail(email);
        this.password = password;
        this.role = role;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void validate() {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BadRequestException("Email should be a valid email address");
        }
        String resolvedName = resolveFullName(fullName, email);
        if (resolvedName.length() < 2 || resolvedName.length() > 100) {
            throw new BadRequestException("Full name must be between 2 and 100 characters");
        }
        if (password == null || password.isBlank()) {
            throw new BadRequestException("Password is required");
        }
        this.fullName = resolvedName;
        this.email = normalizeEmail(email);
    }

    public List<String> getPasswordIssues() {
        List<String> issues = new ArrayList<>();
        if (password == null || password.length() < 12) {
            issues.add("at least 12 characters");
        }
        if (password == null || password.length() > 128) {
            issues.add("no more than 128 characters");
        }
        if (password == null || !password.chars().anyMatch(Character::isUpperCase)) {
            issues.add("one uppercase letter");
        }
        if (password == null || !password.chars().anyMatch(Character::isLowerCase)) {
            issues.add("one lowercase letter");
        }
        if (password == null || !password.chars().anyMatch(Character::isDigit)) {
            issues.add("one number");
        }
        if (password == null || !SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            issues.add("one special character");
        }
        if (password != null && password.chars().anyMatch(Character::isWhitespace)) {
            issues.add("no spaces");
        }
        return issues;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.password = encodedPassword;
        touch();
    }

    public void deactivate() {
        this.active = false;
        this.email = "deleted+" + userId + "-" + email;
        touch();
    }

    public static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    public static String resolveFullName(String provided, String normalizedEmail) {
        if (provided != null && !provided.isBlank()) {
            return provided.trim();
        }
        int atIndex = normalizedEmail.indexOf('@');
        return atIndex > 0 ? normalizedEmail.substring(0, atIndex) : normalizedEmail;
    }

    private void touch() {
        updatedAt = LocalDateTime.now();
    }

    private String normalizeNullable(String value) {
        return value == null ? null : value.trim();
    }
}
