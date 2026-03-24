package com.zeki.demo.service;


import org.springframework.stereotype.Service;
import com.zeki.demo.repository.UserRepository;
import com.zeki.demo.dto.RegisterRequest;
import com.zeki.demo.entity.User;


import java.util.Optional;

@Service

public class UserService{
    private final UserRepository userRepository;

 
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(RegisterRequest request) {
        return userRepository.save(request);
    }
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    

}

