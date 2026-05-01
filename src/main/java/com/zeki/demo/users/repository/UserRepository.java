package com.zeki.demo.users.repository;

import com.zeki.demo.users.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User  user);

    Optional<User> findByUserId(UUID id);

    Optional<User> findByEmail(String email);
}
