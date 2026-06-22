package com.zeki.demo.users.domain.repository;

import com.zeki.demo.users.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findByEmail(String normalizedEmail);

    Optional<User> findActiveByEmail(String normalizedEmail);

    boolean existsByEmail(String normalizedEmail);

    boolean existsActiveByEmail(String normalizedEmail);

    User save(User user);

    List<User> findUsersReadyForHardDelete(LocalDateTime now);

    void hardDeleteById(UUID userId, String email);
}
