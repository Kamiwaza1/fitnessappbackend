package com.zeki.demo.Unit;

import com.zeki.demo.users.security.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JWTServiceTest {

    private JWTService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JWTService();
        ReflectionTestUtils.setField(jwtService, "secret", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3_600_000L);
        userDetails = User.withUsername("user@example.com").password("encoded").authorities("ROLE_USER").build();
    }

    @Test
    void generateTokenIncludesUsernameAndRole() {
        String token = jwtService.generateToken(userDetails, "USER");

        assertEquals("user@example.com", jwtService.extractUsername(token));
        assertEquals("USER", jwtService.extractClaim(token, claims -> claims.get("role", String.class)));
        assertTrue(jwtService.isTokenValid(token, userDetails));
        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    void generateTokenWithoutRoleKeepsTokenValid() {
        String token = jwtService.generateToken(userDetails);

        assertEquals("user@example.com", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, userDetails));
        assertNull(jwtService.extractClaim(token, claims -> claims.get("role", String.class)));
    }

    @Test
    void isTokenValidReturnsFalseForDifferentUser() {
        String token = jwtService.generateToken(userDetails, "USER");
        UserDetails otherUser = User.withUsername("other@example.com").password("encoded").authorities("ROLE_USER").build();

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }
}
