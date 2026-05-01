package com.zeki.demo.users.service;

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
import java.util.Optional;
import java.util.UUID;

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
        UserEntity user = userRepoJPA.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        return userDtoDomain.toUserResponse(user);
    }

    @Override
    public Optional<UserResponse> getUserById(UUID id) {
        return userRepoJPA.findById(id).map(userDtoDomain::toUserResponse);
    }

    @Override
    public UserResponse updateUser(String email, UpdateUserRequest request) {
        String normalizedEmail = UserValidator.normalizeEmail(email);
        UserEntity user = userRepoJPA.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        List<String> passwordIssues = userValidator.getPasswordIssues(request.getPassword());
        if (!passwordIssues.isEmpty()) {
            throw new com.zeki.demo.common.exception.BadRequestException(
                    "Password is not strong enough: " + String.join(", ", passwordIssues));
        }

        UserEntity updated = UserEntity.builder()
                .id(user.getId())
                .fullName(UserValidator.resolveFullName(request.getFullName(), normalizedEmail))
                .email(UserValidator.normalizeEmail(request.getEmail()))
                .password(passwordEncoder.encode(request.getPassword()))
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        UserEntity saved = userRepoJPA.save(updated);
        return userDtoDomain.toUserResponse(saved);
    }
}

