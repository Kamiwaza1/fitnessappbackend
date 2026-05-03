package com.zeki.demo.users.service;

import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.common.exception.ConflictException;
import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.dto.UpdateUserRequest;
import com.zeki.demo.users.mapperDtoDomain.UserDtoDomain;
import com.zeki.demo.users.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDtoDomain userDtoDomain;

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
            throw new BadRequestException("Email is required");
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
                    existingUser.getRole(),
                    existingUser.isActive(),
                    existingUser.getCreatedAt(),
                    existingUser.getUpdatedAt()
            );
            List<String> passwordIssues = passwordCandidate.getPasswordIssues();
            if (!passwordIssues.isEmpty()) {
                throw new BadRequestException("Password is not strong enough: " + String.join(", ", passwordIssues));
            }
            passwordToSave = passwordEncoder.encode(updateUserRequest.password());
        }

        User updatedUser = userDtoDomain.toUpdatedDomain(updateUserRequest, existingUser, passwordToSave);
        updatedUser.validate();
        return userRepository.save(updatedUser);
    }

    @Override
    public void deleteCurrentUser(String email) {
        User existingUser = userRepository.findActiveByEmail(User.normalizeEmail(email))
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        existingUser.deactivate();
        userRepository.save(existingUser);
    }
}
