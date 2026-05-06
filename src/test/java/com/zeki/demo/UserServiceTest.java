package com.zeki.demo;

import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.common.exception.ConflictException;
import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.dto.UpdateUserRequest;
import com.zeki.demo.users.mapperDtoDomain.UserDtoDomain;
import com.zeki.demo.users.repository.UserRepository;
import com.zeki.demo.users.service.UserServiceImpl;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, new UserDtoDomain());
    }

    @Test
    void getCurrentUserNormalizesEmail() {
        User existing = activeUser("test@example.com");
        when(userRepository.findActiveByEmail("test@example.com")).thenReturn(Optional.of(existing));

        User user = userService.getCurrentUser(" TEST@Example.com ");

        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void updateUserWhenEmailTakenThrowsConflict() {
        User existing = activeUser("owner@example.com");
        UpdateUserRequest request = new UpdateUserRequest("Owner", "taken@example.com", null);

        when(userRepository.findActiveByEmail("owner@example.com")).thenReturn(Optional.of(existing));
        when(userRepository.existsActiveByEmail("taken@example.com")).thenReturn(true);

        ConflictException ex = assertThrows(
                ConflictException.class,
                () -> userService.updateUser("owner@example.com", request)
        );

        assertEquals("An account with this email already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserWithStrongPasswordEncodesAndSaves() {
        User existing = activeUser("owner@example.com");
        UpdateUserRequest request = new UpdateUserRequest(
                "Updated User",
                "updated@example.com",
                "StrongPassword123!"
        );

        when(userRepository.findActiveByEmail("owner@example.com")).thenReturn(Optional.of(existing));
        when(userRepository.existsActiveByEmail("updated@example.com")).thenReturn(false);
        when(passwordEncoder.encode("StrongPassword123!")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.updateUser("owner@example.com", request);

        assertEquals("updated@example.com", updated.getEmail());
        assertEquals("encoded-password", updated.getPassword());
        verify(passwordEncoder).encode("StrongPassword123!");
    }

    @Test
    void updateUserWithWeakPasswordThrowsBadRequest() {
        User existing = activeUser("owner@example.com");
        UpdateUserRequest request = new UpdateUserRequest("Updated User", "updated@example.com", "weak");

        when(userRepository.findActiveByEmail("owner@example.com")).thenReturn(Optional.of(existing));
        when(userRepository.existsActiveByEmail("updated@example.com")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> userService.updateUser("owner@example.com", request));
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteCurrentUserSoftDeletesAndSaves() {
        User existing = activeUser("owner@example.com");
        when(userRepository.findActiveByEmail("owner@example.com")).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.deleteCurrentUser("owner@example.com");

        assertFalse(existing.isActive());
        verify(userRepository).save(existing);
    }

    @Test
    void deleteCurrentUserWhenMissingThrowsUnauthorized() {
        when(userRepository.findActiveByEmail("missing@example.com")).thenReturn(Optional.empty());

        UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> userService.deleteCurrentUser("missing@example.com")
        );

        assertEquals("User not found", ex.getMessage());
    }

    private User activeUser(String email) {
        LocalDateTime now = LocalDateTime.now();
        return new User(
                UUID.randomUUID(),
                "Test User",
                email,
                "EncodedPassword123!",
                "USER",
                true,
                now.minusDays(1),
                now
        );
    }
}
