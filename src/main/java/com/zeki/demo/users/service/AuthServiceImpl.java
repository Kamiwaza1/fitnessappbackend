package com.zeki.demo.users.service;

import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.common.exception.ConflictException;
import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.mail.EmailService;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.entity.PasswordResetTokenEntity;
import com.zeki.demo.users.entity.UserEntity;
import com.zeki.demo.users.repository.UserRepository;
import com.zeki.demo.users.repository.jpa.PasswordResetTokenRepoJPA;
import com.zeki.demo.users.repository.jpa.UserRepoJPA;
import com.zeki.demo.users.security.JWTService;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);
    public static final String PASSWORD_RESET_RESPONSE =
            "If this email exists, a reset link has been sent.";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordResetTokenRepoJPA passwordResetTokenRepoJPA;
    private final UserRepoJPA userRepoJPA;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${auth.password-reset.expiration-minutes:30}")
    private long passwordResetExpirationMinutes;

    @Override
    public AuthenticatedUser register(User user) {
        user.validate();
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("An account with this email already exists");
        }
        List<String> passwordIssues = user.getPasswordIssues();
        if (!passwordIssues.isEmpty()) {
            throw new BadRequestException("Password is not strong enough: " + String.join(", ", passwordIssues));
        }
        user.setEncodedPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        emailService.sendWelcomeEmail(savedUser);
        UserDetails userDetails = toUserDetails(savedUser);
        String accessToken = jwtService.generateAccessToken(userDetails, savedUser.getRole());
        String refreshToken = jwtService.generateRefreshToken(savedUser.getEmail());
        return new AuthenticatedUser(savedUser, accessToken, refreshToken);
    }

    @Override
    public AuthenticatedUser login(User user) {
        String normalizedEmail = User.normalizeEmail(user.getEmail());
        if (normalizedEmail.isBlank()) {
            throw new BadRequestException("Email is required");
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedEmail, user.getPassword())
            );
        } catch (AuthenticationException exception) {
            throw new UnauthorizedException("Invalid email or password");
        }
        User existingUser = userRepository.findActiveByEmail(normalizedEmail)
                .orElseThrow(() -> new UnauthorizedException("No account found with this email"));
        UserDetails userDetails = toUserDetails(existingUser);
        String accessToken = jwtService.generateAccessToken(userDetails, existingUser.getRole());
        String refreshToken = jwtService.generateRefreshToken(existingUser.getEmail());
        return new AuthenticatedUser(existingUser, accessToken, refreshToken);
    }

    @Override
    public AuthenticatedUser refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadRequestException("Refresh token is required");
        }

        try {
            String email = jwtService.extractRefreshUsername(refreshToken);
            User existingUser = userRepository.findActiveByEmail(email)
                    .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
            if (!jwtService.isRefreshTokenValid(refreshToken, existingUser.getEmail())) {
                throw new UnauthorizedException("Invalid refresh token");
            }

            UserDetails userDetails = toUserDetails(existingUser);
            String newAccessToken = jwtService.generateAccessToken(userDetails, existingUser.getRole());
            String newRefreshToken = jwtService.generateRefreshToken(existingUser.getEmail());
            return new AuthenticatedUser(existingUser, newAccessToken, newRefreshToken);
        } catch (JwtException | IllegalArgumentException exception) {
            throw new UnauthorizedException("Invalid refresh token");
        }
    }

    @Override
    public void requestPasswordReset(String email) {
        String normalizedEmail = User.normalizeEmail(email);
        if (normalizedEmail.isBlank()) {
            throw new BadRequestException("Email is required");
        }

        userRepository.findActiveByEmail(normalizedEmail).ifPresentOrElse(user -> {
            LocalDateTime now = LocalDateTime.now();
            passwordResetTokenRepoJPA
                    .findAllByUserUserIdAndUsedAtIsNullAndExpiresAtAfter(user.getUserId(), now)
                    .forEach(token -> token.markUsed(now));

            String rawToken = generateResetToken();
            String securityCode = generateSecurityCode();
            PasswordResetTokenEntity tokenEntity = PasswordResetTokenEntity.builder()
                    .user(toUserEntityReference(user))
                    .tokenHash(hashToken(rawToken))
                    .codeHash(hashToken(securityCode))
                    .expiresAt(now.plusMinutes(passwordResetExpirationMinutes))
                    .createdAt(now)
                    .build();

            passwordResetTokenRepoJPA.save(tokenEntity);
            LOGGER.info("Password reset token created for user {}", user.getUserId());
            emailService.sendPasswordResetEmail(
                    user,
                    buildResetUrl(rawToken),
                    securityCode,
                    passwordResetExpirationMinutes
            );
        }, () -> LOGGER.info("Password reset requested for an email without an active account"));
    }

    @Override
    public void resetPassword(String token, String code, String newPassword) {
        if (token == null || token.isBlank()) {
            throw new BadRequestException("Reset token is required");
        }
        if (code == null || !code.matches("\\d{6}")) {
            throw new BadRequestException("Security code must be 6 digits");
        }

        LocalDateTime now = LocalDateTime.now();
        PasswordResetTokenEntity tokenEntity = passwordResetTokenRepoJPA.findByTokenHash(hashToken(token))
                .filter(candidate -> candidate.isUsable(now))
                .filter(candidate -> hashToken(code).equals(candidate.getCodeHash()))
                .orElseThrow(() -> new BadRequestException("Password reset link is invalid or expired"));

        User existingUser = userRepository.findActiveByEmail(tokenEntity.getUser().getEmail())
                .orElseThrow(() -> new BadRequestException("Password reset link is invalid or expired"));

        User passwordCandidate = new User(
                existingUser.getUserId(),
                existingUser.getFullName(),
                existingUser.getEmail(),
                newPassword,
                existingUser.getDailyCalorieTarget(),
                existingUser.getRole(),
                existingUser.isActive(),
                existingUser.getCreatedAt(),
                existingUser.getUpdatedAt()
        );
        List<String> passwordIssues = passwordCandidate.getPasswordIssues();
        if (!passwordIssues.isEmpty()) {
            throw new BadRequestException("Password is not strong enough: " + String.join(", ", passwordIssues));
        }

        existingUser.setEncodedPassword(passwordEncoder.encode(newPassword));
        userRepository.save(existingUser);
        tokenEntity.markUsed(now);
        passwordResetTokenRepoJPA.save(tokenEntity);
    }

    private UserDetails toUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole())
                .build();
    }

    private String generateResetToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String generateSecurityCode() {
        return "%06d".formatted(SECURE_RANDOM.nextInt(1_000_000));
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    private String buildResetUrl(String rawToken) {
        String normalizedFrontendUrl = frontendUrl == null || frontendUrl.isBlank()
                ? "http://localhost:3000"
                : frontendUrl.trim().replaceAll("/+$", "");
        return normalizedFrontendUrl
                + "/reset-password?token="
                + URLEncoder.encode(rawToken, StandardCharsets.UTF_8);
    }

    private UserEntity toUserEntityReference(User user) {
        return userRepoJPA.getReferenceById(user.getUserId());
    }
}
