package com.zeki.demo.users.domain;

import com.zeki.demo.users.entity.User;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class UserDomainService {

    private static final String DEFAULT_ROLE = "USER";

    public String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    public String resolveFullName(String providedFullName, String normalizedEmail) {
        if (providedFullName != null && !providedFullName.isBlank()) {
            return providedFullName.trim();
        }

        int atIndex = normalizedEmail.indexOf('@');
        return atIndex > 0 ? normalizedEmail.substring(0, atIndex) : normalizedEmail;
    }

    public User buildRegisteredUser(String fullName, String normalizedEmail, String encodedPassword) {
        return User.builder()
                .fullName(fullName)
                .email(normalizedEmail)
                .password(encodedPassword)
                .role(DEFAULT_ROLE)
                .build();
    }
}
