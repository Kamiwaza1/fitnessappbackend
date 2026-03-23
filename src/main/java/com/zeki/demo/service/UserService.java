package com.zeki.demo.service;


import org.springframework.stereotype.Service;
import com.zeki.demo.repository.UserRepository;
import com.zeki.demo.entity.User;


import java.util.Optional;

@Service

public class UserService{
    private final UserRepository userRepository;

 
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {
        return userRepository.save(user);
    }
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    

}

