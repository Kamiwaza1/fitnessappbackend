package com.zeki.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.zeki.demo.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    
}
