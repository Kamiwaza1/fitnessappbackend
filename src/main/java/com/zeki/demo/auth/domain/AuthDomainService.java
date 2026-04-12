package com.zeki.demo.auth.domain;

import com.zeki.demo.users.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class AuthDomainService {

    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^A-Za-z0-9]");

    public List<String> getPasswordIssues(String password) {
        List<String> issues = new ArrayList<>();

        if (password == null || password.length() < 12) {
            issues.add("at least 12 characters");
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

    public UserDetails toUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole())
                .build();
    }
}
