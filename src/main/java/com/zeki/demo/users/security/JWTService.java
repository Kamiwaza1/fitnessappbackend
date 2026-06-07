package com.zeki.demo.users.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${jwt.access.secret}")
    private String accessSecret;

    @Value("${jwt.access.expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh.secret}")
    private String refreshSecret;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(UserDetails userDetails, String role) {
        return generateAccessToken(userDetails.getUsername(), role);
    }

    public String generateAccessToken(String email) {
        return generateAccessToken(email, null);
    }

    public String generateAccessToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access");
        if (role != null && !role.isBlank()) {
            claims.put("role", role);
        }
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessExpiration);
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getAccessSignInKey())
                .compact();
    }

    public String generateRefreshToken(String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshExpiration);
        return Jwts.builder()
                .subject(email)
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getRefreshSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        String tokenType = extractClaim(token, claims -> claims.get("type", String.class));
        return username.equals(userDetails.getUsername())
                && "access".equals(tokenType)
                && !isTokenExpired(token);
    }

    public String extractRefreshUsername(String token) {
        return extractRefreshClaim(token, Claims::getSubject);
    }

    public <T> T extractRefreshClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllRefreshClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isRefreshTokenValid(String token, String email) {
        String username = extractRefreshUsername(token);
        String tokenType = extractRefreshClaim(token, claims -> claims.get("type", String.class));
        return username.equals(email)
                && "refresh".equals(tokenType)
                && !isRefreshTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public boolean isRefreshTokenExpired(String token) {
        return extractRefreshClaim(token, Claims::getExpiration).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getAccessSignInKey()).build()
                .parseSignedClaims(token).getPayload();
    }

    private Claims extractAllRefreshClaims(String token) {
        return Jwts.parser().verifyWith(getRefreshSignInKey()).build()
                .parseSignedClaims(token).getPayload();
    }

    private SecretKey getAccessSignInKey() {
        return Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
    }

    private SecretKey getRefreshSignInKey() {
        return Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
    }
}
