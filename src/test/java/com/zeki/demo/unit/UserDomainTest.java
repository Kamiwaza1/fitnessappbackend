package com.zeki.demo.unit;

import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.users.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserDomainTest {

    @Test
    void validateNormalizesEmailAndDerivesMissingName() {
        User user = user(null, "  USER@Example.com  ", "StrongPassword123!");

        user.validate();

        assertEquals("user@example.com", user.getEmail());
        assertEquals("user", user.getFullName());
    }

    @Test
    void validateRejectsInvalidEmail() {
        User user = user("Name", "not-an-email", "StrongPassword123!");

        BadRequestException ex = assertThrows(BadRequestException.class, user::validate);

        assertEquals("Email should be a valid email address", ex.getMessage());
    }

    @Test
    void getPasswordIssuesListsAllExpectedProblems() {
        User user = user("Name", "user@example.com", "weak pass");

        List<String> issues = user.getPasswordIssues();

        assertTrue(issues.contains("at least 12 characters"));
        assertTrue(issues.contains("one uppercase letter"));
        assertTrue(issues.contains("one number"));
        assertTrue(issues.contains("no spaces"));
    }

    @Test
    void deactivateMarksUserInactiveAndPrefixesEmail() {
        User user = user("Name", "user@example.com", "StrongPassword123!");

        user.deactivate();

        assertFalse(user.isActive());
        assertTrue(user.getEmail().startsWith("deleted+" + user.getUserId() + "-"));
    }

    @Test
    void setEncodedPasswordReplacesRawPasswordAndTouchesUpdatedAt() {
        User user = user("Name", "user@example.com", "StrongPassword123!");
        LocalDateTime before = user.getUpdatedAt();

        user.setEncodedPassword("encoded-password");

        assertEquals("encoded-password", user.getPassword());
        assertNotEquals(before, user.getUpdatedAt());
    }

    private User user(String fullName, String email, String password) {
        return new User(
                UUID.randomUUID(),
                fullName,
                email,
                password,
                null,
                "USER",
                true,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1)
        );
    }
}
