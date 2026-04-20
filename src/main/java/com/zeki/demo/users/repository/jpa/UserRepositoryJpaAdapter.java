package com.zeki.demo.users.repository.jpa;

import com.zeki.demo.users.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryJpaAdapter implements com.zeki.demo.users.repository.UserRepository {

    private final UserRepoJPA userRepoJPA;

    public UserRepositoryJpaAdapter(UserRepoJPA userRepoJPA) {
        this.userRepoJPA = userRepoJPA;
    }

    @Override
    public User save(User user) {
        return userRepoJPA.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepoJPA.findById(id);
    }

    @Override
    public Optional<User> findByEmailIgnoreCase(String email) {
        return userRepoJPA.findByEmailIgnoreCase(email);
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return userRepoJPA.existsByEmailIgnoreCase(email);
    }
}
