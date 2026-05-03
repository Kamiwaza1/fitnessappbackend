package com.zeki.demo.users.service;

import com.zeki.demo.users.repository.JPA.UserRepoJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepoJPA userRepoJPA;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepoJPA.findByEmailIgnoreCaseAndIsActiveTrue(email)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities("ROLE_" + user.getRole())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
