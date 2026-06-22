package com.zeki.demo.users.service;

import com.zeki.demo.common.exception.ConflictException;
import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.mail.EmailService;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.dto.UpdateUserRequest;
import com.zeki.demo.users.exception.UserValidationException;
import com.zeki.demo.users.mapperdtodomain.UserDtoDomain;
import com.zeki.demo.users.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private static final long ACCOUNT_DELETION_GRACE_PERIOD_DAYS = 7;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDtoDomain userDtoDomain;
    private final EmailService emailService;

    @Value("${app.public-frontend-url:https://darklift.app}")
    private String publicFrontendUrl;

    @Value("${app.local-frontend-url:http://localhost:3000}")
    private String localFrontendUrl;

    @Override
    public User getCurrentUser(String email) {
        return userRepository.findActiveByEmail(User.normalizeEmail(email))
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @Override
    public User updateUser(String email, UpdateUserRequest updateUserRequest) {
        User existingUser = userRepository.findActiveByEmail(User.normalizeEmail(email))
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        String updatedEmail = updateUserRequest.email() != null
                ? User.normalizeEmail(updateUserRequest.email())
                : existingUser.getEmail();
        if (updatedEmail.isBlank()) {
            throw new UserValidationException("Email is required");
        }
        if (!existingUser.getEmail().equals(updatedEmail) && userRepository.existsActiveByEmail(updatedEmail)) {
            throw new ConflictException("An account with this email already exists");
        }

        String passwordToSave = existingUser.getPassword();
        if (updateUserRequest.password() != null) {
            User passwordCandidate = new User(
                    existingUser.getUserId(),
                    updateUserRequest.fullName() != null ? updateUserRequest.fullName() : existingUser.getFullName(),
                    updatedEmail,
                    updateUserRequest.password(),
                    updateUserRequest.dailyCalorieTarget() != null
                            ? updateUserRequest.dailyCalorieTarget()
                            : existingUser.getDailyCalorieTarget(),
                    existingUser.getRole(),
                    existingUser.isActive(),
                    existingUser.getCreatedAt(),
                    existingUser.getUpdatedAt(),
                    existingUser.getDeletionRequestedAt(),
                    existingUser.getDeletionScheduledFor()
            );
            List<String> passwordIssues = passwordCandidate.getPasswordIssues();
            if (!passwordIssues.isEmpty()) {
                throw new UserValidationException("Password is not strong enough: " + String.join(", ", passwordIssues));
            }
            passwordToSave = passwordEncoder.encode(updateUserRequest.password());
        }

        User updatedUser = userDtoDomain.toUpdatedDomain(updateUserRequest, existingUser, passwordToSave);
        updatedUser.validate();
        return userRepository.save(updatedUser);
    }

    @Override
    public void deleteCurrentUser(String email, String password) {
        if (password == null || password.isBlank()) {
            throw new UserValidationException("Password is required");
        }
        User existingUser = userRepository.findActiveByEmail(User.normalizeEmail(email))
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        if (!passwordEncoder.matches(password, existingUser.getPassword())) {
            throw new UnauthorizedException("Invalid password");
        }
        LocalDateTime now = LocalDateTime.now();
        existingUser.requestDeletion(now, now.plusDays(ACCOUNT_DELETION_GRACE_PERIOD_DAYS));
        userRepository.save(existingUser);
        emailService.sendAccountDeletionEmail(
                existingUser,
                buildLoginUrl(publicFrontendUrl),
                buildLoginUrl(localFrontendUrl),
                ACCOUNT_DELETION_GRACE_PERIOD_DAYS
        );
    }

    @Override
    public void cancelDeletion(String email) {
        User existingUser = userRepository.findActiveByEmail(User.normalizeEmail(email))
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        if (existingUser.isDeletionPending()) {
            existingUser.cancelDeletion();
            userRepository.save(existingUser);
        }
    }

    @Override
    @Scheduled(cron = "${auth.account-deletion.purge-cron:0 0 3 * * *}")
    public void hardDeleteExpiredAccounts() {
        LocalDateTime now = LocalDateTime.now();
        userRepository.findUsersReadyForHardDelete(now)
                .forEach(user -> userRepository.hardDeleteById(user.getUserId(), user.getEmail()));
    }

    private String buildLoginUrl(String frontendUrl) {
        String normalizedFrontendUrl = frontendUrl == null || frontendUrl.isBlank()
                ? "http://localhost:3000"
                : removeTrailingSlashes(frontendUrl.trim());
        return normalizedFrontendUrl + "/login";
    }

    private String removeTrailingSlashes(String value) {
        int endIndex = value.length();
        while (endIndex > 0 && value.charAt(endIndex - 1) == '/') {
            endIndex--;
        }
        return value.substring(0, endIndex);
    }
}
