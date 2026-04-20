package com.zeki.demo.users.repository;

import com.zeki.demo.users.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
