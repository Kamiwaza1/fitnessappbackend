package com.zeki.demo.users.repository.JPA;

import com.zeki.demo.users.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepoJPA extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmailIgnoreCase(String email);
    Optional<UserEntity> findByEmailIgnoreCaseAndIsActiveTrue(String email);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIsActiveTrue(String email);
}
