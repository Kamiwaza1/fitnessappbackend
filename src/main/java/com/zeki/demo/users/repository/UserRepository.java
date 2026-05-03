package com.zeki.demo.users.repository;

import com.zeki.demo.users.domain.User;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(String normalizedEmail);

    Optional<User> findActiveByEmail(String normalizedEmail);

    boolean existsByEmail(String normalizedEmail);

    boolean existsActiveByEmail(String normalizedEmail);

    User save(User user);
}
