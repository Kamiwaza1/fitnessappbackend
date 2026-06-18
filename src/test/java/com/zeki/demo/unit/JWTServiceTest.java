package com.zeki.demo.unit;

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
        ReflectionTestUtils.setField(jwtService, "accessSecret", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtService, "accessExpiration", 3_600_000L);
        ReflectionTestUtils.setField(jwtService, "refreshSecret", "abcdefghijklmnopqrstuvwxyz123456");
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 604_800_000L);
        userDetails = User.withUsername("user@example.com").password("encoded").authorities("ROLE_USER").build();
    }

    @Test
    void generateAccessTokenIncludesUsernameRoleAndType() {
        String token = jwtService.generateAccessToken(userDetails, "USER");

        assertEquals("user@example.com", jwtService.extractUsername(token));
        assertEquals("USER", jwtService.extractClaim(token, claims -> claims.get("role", String.class)));
        assertEquals("access", jwtService.extractClaim(token, claims -> claims.get("type", String.class)));
        assertTrue(jwtService.isTokenValid(token, userDetails));
        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    void generateAccessTokenWithoutRoleKeepsTokenValid() {
        String token = jwtService.generateAccessToken("user@example.com");

        assertEquals("user@example.com", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, userDetails));
        assertNull(jwtService.extractClaim(token, claims -> claims.get("role", String.class)));
    }

    @Test
    void isTokenValidReturnsFalseForDifferentUser() {
        String token = jwtService.generateAccessToken(userDetails, "USER");
        UserDetails otherUser = User.withUsername("other@example.com").password("encoded").authorities("ROLE_USER").build();

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    void generateRefreshTokenIncludesUsernameAndRefreshType() {
        String token = jwtService.generateRefreshToken("user@example.com");

        assertEquals("user@example.com", jwtService.extractRefreshUsername(token));
        assertEquals("refresh", jwtService.extractRefreshClaim(token, claims -> claims.get("type", String.class)));
        assertTrue(jwtService.isRefreshTokenValid(token, "user@example.com"));
        assertFalse(jwtService.isRefreshTokenExpired(token));
    }
}
