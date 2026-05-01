package com.zeki.demo.users.service;

import com.zeki.demo.users.domain.UserValidator;
import com.zeki.demo.users.repository.jpa.UserRepoJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepoJPA userRepoJPA;
    private final UserValidator userValidator;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepoJPA.findByEmailIgnoreCase(email)
                .map(userValidator::toUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
