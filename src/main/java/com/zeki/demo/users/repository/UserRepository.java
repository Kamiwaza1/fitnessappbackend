package com.zeki.demo.users.repository;

import com.zeki.demo.users.entity.UserEntity;


import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    UserEntity save(UserEntity user);
    Optional<UserEntity> findById(UUID id);
    Optional<UserEntity> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
