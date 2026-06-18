package com.zeki.demo.users.repository.jpa;

import com.zeki.demo.users.entity.PasswordResetTokenEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepoJPA extends JpaRepository<PasswordResetTokenEntity, UUID> {
    Optional<PasswordResetTokenEntity> findByTokenHash(String tokenHash);

    List<PasswordResetTokenEntity> findAllByUserUserIdAndUsedAtIsNullAndExpiresAtAfter(
            UUID userId,
            LocalDateTime now
    );
}
