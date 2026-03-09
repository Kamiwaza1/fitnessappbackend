package com.zeki.demo.user;
import org.springframework.data.jpa.repository.JpaRepository;


import com.zeki.demo.user.dto.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}