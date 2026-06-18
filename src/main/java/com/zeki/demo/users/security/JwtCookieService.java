package com.zeki.demo.users.security;

import com.zeki.demo.users.service.AuthenticatedUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class JwtCookieService {

    @Getter
    @Value("${auth.cookies.access-token-name:ACCESS_TOKEN}")
    private String accessTokenCookieName;

    @Getter
    @Value("${auth.cookies.refresh-token-name:REFRESH_TOKEN}")
    private String refreshTokenCookieName;

    @Value("${jwt.access.expiration:900000}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh.expiration:604800000}")
    private long refreshTokenExpirationMs;

    @Value("${auth.cookies.secure:false}")
    private boolean secure;

    @Value("${auth.cookies.same-site:Lax}")
    private String sameSite;

    public void addAuthCookies(HttpServletResponse response, AuthenticatedUser authenticatedUser) {
        if (response == null || authenticatedUser == null) {
            return;
        }
        addCookie(response, accessTokenCookieName, authenticatedUser.accessToken(), accessTokenExpirationMs);
        addCookie(response, refreshTokenCookieName, authenticatedUser.refreshToken(), refreshTokenExpirationMs);
    }

    public void clearAuthCookies(HttpServletResponse response) {
        if (response == null) {
            return;
        }
        addCookie(response, accessTokenCookieName, "", 0);
        addCookie(response, refreshTokenCookieName, "", 0);
    }

    public Optional<String> getAccessToken(HttpServletRequest request) {
        return getCookieValue(request, accessTokenCookieName);
    }

    public Optional<String> getRefreshToken(HttpServletRequest request) {
        return getCookieValue(request, refreshTokenCookieName);
    }

    private void addCookie(HttpServletResponse response, String name, String value, long maxAgeMs) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(Duration.ofMillis(maxAgeMs))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private Optional<String> getCookieValue(HttpServletRequest request, String name) {
        if (request == null || request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}
