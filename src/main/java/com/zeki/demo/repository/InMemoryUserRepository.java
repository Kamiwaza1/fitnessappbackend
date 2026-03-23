package com.zeki.demo.repository;

import org.springframework.data.domain.*;

import com.zeki.demo.entity.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> userStore = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.incrementAndGet());
        }
        userStore.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userStore.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userStore.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userStore.values());
    }

    @Override
    public void deleteById(Long id) {
        userStore.remove(id);
    }

    // Implement JpaRepository methods
    @Override
    public List<User> findAll(Sort sort) {
        // Sorting logic can be implemented here if needed
        return findAll();
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example) {
        // Example matching logic can be implemented here if needed
        return new ArrayList<>();
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
        // Example matching with sorting logic can be implemented here if needed
        return new ArrayList<>();
    }

    // Add other JpaRepository methods as needed
}