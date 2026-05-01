package com.zeki.demo.users.repository;

import com.zeki.demo.users.entity.UserEntity;
import com.zeki.demo.users.repository.jpa.UserRepoJPA;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserRepoJPA userRepoJPA;
    private final UserEntityDomain userEntityDomain;

    public UserRepositoryImpl(UserRepoJPA userRepoJPA, UserEntityDomain userEntityDomain) {
        this.userRepoJPA = userRepoJPA;
        this.userEntityDomain = userEntityDomain;
    }

    @Override
    public User save(User user) {
        return userEntityDomain.toDomain(userRepoJPA.save(userEntityDomain.toEntity(user)));
    }

    @Override
    public Optional<User> findByUserId(UUID id) {
        return userRepoJPA.findById(id)
                .map(userEntityDomain::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepoJPA.findByEmailIgnoreCase(email)
                .map(userEntityDomain::toDomain);
    }

}
