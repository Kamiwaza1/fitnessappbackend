package com.zeki.demo.users.service;

import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.common.exception.ConflictException;
import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.users.domain.UserValidator;
import com.zeki.demo.users.dto.UpdateUserRequest;
import com.zeki.demo.users.dto.UserResponse;
import com.zeki.demo.users.entity.UserEntity;
import com.zeki.demo.users.mapperDtoDomain.UserDtoDomain;
import com.zeki.demo.users.repository.jpa.UserRepoJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepoJPA userRepoJPA;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final UserDtoDomain userDtoDomain;

    @Override
    public UserResponse getCurrentUser(String email) {
        String normalizedEmail = UserValidator.normalizeEmail(email);
        UserEntity user = userRepoJPA.findByEmailIgnoreCaseAndIsActiveTrue(normalizedEmail)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        return userDtoDomain.toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(String email, UpdateUserRequest request) {
        String normalizedEmail = UserValidator.normalizeEmail(email);
        UserEntity user = userRepoJPA.findByEmailIgnoreCaseAndIsActiveTrue(normalizedEmail)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        String updatedEmail = UserValidator.normalizeEmail(request.getEmail());
        if (updatedEmail.isBlank()) {
            throw new BadRequestException("Email is required");
        }

        if (!normalizedEmail.equals(updatedEmail)
                && userRepoJPA.existsByEmailIgnoreCaseAndIsActiveTrue(updatedEmail)) {
            throw new ConflictException("An account with this email already exists");
        }

        List<String> passwordIssues = userValidator.getPasswordIssues(request.getPassword());
        if (!passwordIssues.isEmpty()) {
            throw new BadRequestException(
                    "Password is not strong enough: " + String.join(", ", passwordIssues));
        }

        user.setFullName(UserValidator.resolveFullName(request.getFullName(), updatedEmail));
        user.setEmail(updatedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        UserEntity saved = userRepoJPA.save(user);
        return userDtoDomain.toUserResponse(saved);
    }

    @Override
    public void deleteCurrentUser(String email) {
        String normalizedEmail = UserValidator.normalizeEmail(email);
        UserEntity user = userRepoJPA.findByEmailIgnoreCaseAndIsActiveTrue(normalizedEmail)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        user.setIsActive(false);
        user.setEmail("deleted+" + user.getId() + "-" + normalizedEmail);
        userRepoJPA.save(user);
    }
}
