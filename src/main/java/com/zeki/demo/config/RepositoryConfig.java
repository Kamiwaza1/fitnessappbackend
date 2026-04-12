package com.zeki.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.zeki.demo.repository.UserRepository;
import com.zeki.demo.repository.InMemoryUserRepository;

@Configuration
public class RepositoryConfig {
    @Bean 
public UserRepository userRepository() {
        return new InMemoryUserRepository();
    }
}
//Switch between JPA and in-memory repository by commenting out the appropriate @Bean method in RepositoryConfig.