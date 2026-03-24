package com.zeki.demo.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import com.zeki.demo.dto.RegisterRequest;
import com.zeki.demo.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(RegisterRequest request);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    void deleteById(Long id);
    List<User> findAll(Sort sort);
    <S extends User> List<S> findAll(Example<S> example);
    <S extends User> List<S> findAll(Example<S> example, Sort sort);
    User save(User user);
}
