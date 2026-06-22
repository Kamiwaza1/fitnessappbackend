package com.zeki.demo.unit;

import com.zeki.demo.users.exception.UserValidationException;
import com.zeki.demo.common.exception.ConflictException;
import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.mail.EmailService;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.dto.UpdateUserRequest;
import com.zeki.demo.users.mapperdtodomain.UserDtoDomain;
import com.zeki.demo.users.domain.repository.UserRepository;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final String OWNER_EMAIL = "owner@example.com";
    private static final String UPDATED_EMAIL = "updated@example.com";
    private static final String TAKEN_EMAIL = "taken@example.com";
    private static final String STRONG_PASSWORD = "StrongPassword123!";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, new UserDtoDomain(), emailService);
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
        User existing = activeUser(OWNER_EMAIL);
        UpdateUserRequest request = updateRequest("Owner", TAKEN_EMAIL, null);

        when(userRepository.findActiveByEmail(OWNER_EMAIL)).thenReturn(Optional.of(existing));
        when(userRepository.existsActiveByEmail(TAKEN_EMAIL)).thenReturn(true);

        ConflictException ex = assertThrows(
                ConflictException.class,
                () -> userService.updateUser(OWNER_EMAIL, request)
        );

        assertEquals("An account with this email already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserWithStrongPasswordEncodesAndSaves() {
        User existing = activeUser(OWNER_EMAIL);
        UpdateUserRequest request = updateRequest("Updated User", UPDATED_EMAIL, STRONG_PASSWORD);

        when(userRepository.findActiveByEmail(OWNER_EMAIL)).thenReturn(Optional.of(existing));
        when(userRepository.existsActiveByEmail(UPDATED_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(STRONG_PASSWORD)).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.updateUser(OWNER_EMAIL, request);

        assertEquals(UPDATED_EMAIL, updated.getEmail());
        assertEquals("encoded-password", updated.getPassword());
        verify(passwordEncoder).encode(STRONG_PASSWORD);
    }

    @Test
    void updateUserWithWeakPasswordThrowsBadRequest() {
        User existing = activeUser(OWNER_EMAIL);
        UpdateUserRequest request = updateRequest("Updated User", UPDATED_EMAIL, "weak");

        when(userRepository.findActiveByEmail(OWNER_EMAIL)).thenReturn(Optional.of(existing));
        when(userRepository.existsActiveByEmail(UPDATED_EMAIL)).thenReturn(false);

        assertThrows(UserValidationException.class, () -> userService.updateUser(OWNER_EMAIL, request));
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserWithBlankEmailThrowsBadRequest() {
        User existing = activeUser(OWNER_EMAIL);
        UpdateUserRequest request = updateRequest("Updated User", "   ", STRONG_PASSWORD);

        when(userRepository.findActiveByEmail(OWNER_EMAIL)).thenReturn(Optional.of(existing));

        UserValidationException ex = assertThrows(UserValidationException.class, () -> userService.updateUser(OWNER_EMAIL, request));

        assertEquals("Email is required", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserWithSameEmailDoesNotCheckUniquenessAndSaves() {
        User existing = activeUser(OWNER_EMAIL);
        UpdateUserRequest request = updateRequest("Updated User", " OWNER@example.com ", null);

        when(userRepository.findActiveByEmail(OWNER_EMAIL)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.updateUser(OWNER_EMAIL, request);

        assertEquals(OWNER_EMAIL, updated.getEmail());
        verify(userRepository, never()).existsActiveByEmail(OWNER_EMAIL);
    }

    @Test
    void updateUserSavesDailyCalorieTarget() {
        User existing = activeUser(OWNER_EMAIL);
        UpdateUserRequest request = new UpdateUserRequest(null, null, null, 2600);

        when(userRepository.findActiveByEmail(OWNER_EMAIL)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.updateUser(OWNER_EMAIL, request);

        assertEquals(2600, updated.getDailyCalorieTarget());
    }

    @Test
    void deleteCurrentUserSchedulesDeletionAndSendsEmail() {
        User existing = activeUser(OWNER_EMAIL);
        when(userRepository.findActiveByEmail(OWNER_EMAIL)).thenReturn(Optional.of(existing));
        when(passwordEncoder.matches(STRONG_PASSWORD, existing.getPassword())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.deleteCurrentUser(OWNER_EMAIL, STRONG_PASSWORD);

        assertTrue(existing.isActive());
        assertNotNull(existing.getDeletionRequestedAt());
        assertNotNull(existing.getDeletionScheduledFor());
        verify(userRepository).save(existing);
        verify(emailService).sendAccountDeletionEmail(
                org.mockito.ArgumentMatchers.eq(existing),
                org.mockito.ArgumentMatchers.endsWith("/login"),
                org.mockito.ArgumentMatchers.eq("http://localhost:3000/login"),
                org.mockito.ArgumentMatchers.eq(7L)
        );
    }

    @Test
    void deleteCurrentUserWhenMissingThrowsUnauthorized() {
        when(userRepository.findActiveByEmail("missing@example.com")).thenReturn(Optional.empty());

        UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> userService.deleteCurrentUser("missing@example.com", STRONG_PASSWORD)
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
                null,
                "USER",
                true,
                now.minusDays(1),
                now
        );
    }

    private UpdateUserRequest updateRequest(String name, String email, String password) {
        return new UpdateUserRequest(name, email, password, null);
    }
}
