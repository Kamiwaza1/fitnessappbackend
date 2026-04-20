package com.zeki.demo.users.repository.jpa;

import com.zeki.demo.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepoJPA extends JpaRepository<User, UUID> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
