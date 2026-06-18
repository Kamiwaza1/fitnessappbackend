package com.zeki.demo.users.mapperdomainentity;

import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.entity.UserEntity;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class UserDomainEntity {

    public User toDomain(UserEntity entity) {
        return new User(
                entity.getUserId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getDailyCalorieTarget(),
                entity.getFitnessGoal(),
                entity.getExperienceLevel(),
                entity.getRole(),
                Boolean.TRUE.equals(entity.getIsActive()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletionRequestedAt(),
                entity.getDeletionScheduledFor()
        );
    }

    public UserEntity toEntity(User user) {
        LocalDateTime now = LocalDateTime.now();
        return UserEntity.builder()
                .userId(user.getUserId())
                .fullName(User.resolveFullName(user.getFullName(), user.getEmail()))
                .email(user.getEmail())
                .password(user.getPassword())
                .dailyCalorieTarget(user.getDailyCalorieTarget())
                .fitnessGoal(user.getFitnessGoal())
                .experienceLevel(user.getExperienceLevel())
                .role(user.getRole())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt() : now)
                .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt() : now)
                .deletionRequestedAt(user.getDeletionRequestedAt())
                .deletionScheduledFor(user.getDeletionScheduledFor())
                .build();
    }
}
