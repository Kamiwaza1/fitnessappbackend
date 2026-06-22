package com.zeki.demo.unit;

import com.zeki.demo.common.config.CacheConfig;
import com.zeki.demo.common.exception.GlobalExceptionHandler;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.security.JwtCookieService;
import com.zeki.demo.users.security.RestAccessDeniedHandler;
import com.zeki.demo.users.security.RestAuthenticationEntryPoint;
import com.zeki.demo.users.service.AuthenticatedUser;
import jakarta.servlet.http.Cookie;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class SecurityAndExceptionCoverageTest {

    @Test
    void jwtCookieServiceAddsReadsAndClearsAuthCookies() {
        JwtCookieService service = jwtCookieService();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                user(),
                "access-token",
                "refresh-token"
        );

        service.addAuthCookies(response, authenticatedUser);

        String setCookieHeaders = String.join("\n", response.getHeaders("Set-Cookie"));
        assertTrue(setCookieHeaders.contains("ACCESS_TOKEN=access-token"));
        assertTrue(setCookieHeaders.contains("REFRESH_TOKEN=refresh-token"));
        assertTrue(setCookieHeaders.contains("HttpOnly"));
        assertTrue(setCookieHeaders.contains("SameSite=Lax"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("ACCESS_TOKEN", "access-token"), new Cookie("REFRESH_TOKEN", "refresh-token"));

        assertEquals("access-token", service.getAccessToken(request).orElseThrow());
        assertEquals("refresh-token", service.getRefreshToken(request).orElseThrow());
        assertTrue(service.getAccessToken(null).isEmpty());

        MockHttpServletResponse clearResponse = new MockHttpServletResponse();
        service.clearAuthCookies(clearResponse);
        String clearHeaders = String.join("\n", clearResponse.getHeaders("Set-Cookie"));
        assertTrue(clearHeaders.contains("ACCESS_TOKEN="));
        assertTrue(clearHeaders.contains("REFRESH_TOKEN="));
        assertTrue(clearHeaders.contains("Max-Age=0"));
    }

    @Test
    void jwtCookieServiceIgnoresNullInputs() {
        JwtCookieService service = jwtCookieService();
        MockHttpServletResponse response = new MockHttpServletResponse();

        service.addAuthCookies(response, null);
        service.clearAuthCookies(null);

        assertTrue(response.getHeaders("Set-Cookie").isEmpty());
        assertTrue(service.getRefreshToken(new MockHttpServletRequest()).isEmpty());
    }

    @Test
    void restSecurityHandlersWriteJsonErrors() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/workouts");
        MockHttpServletResponse unauthorizedResponse = new MockHttpServletResponse();
        MockHttpServletResponse forbiddenResponse = new MockHttpServletResponse();

        new RestAuthenticationEntryPoint().commence(
                request,
                unauthorizedResponse,
                new BadCredentialsException("bad")
        );
        new RestAccessDeniedHandler().handle(
                request,
                forbiddenResponse,
                new AccessDeniedException("denied")
        );

        assertEquals(HttpStatus.UNAUTHORIZED.value(), unauthorizedResponse.getStatus());
        assertTrue(unauthorizedResponse.getContentAsString().contains("Login required or token is invalid/expired"));
        assertEquals(HttpStatus.FORBIDDEN.value(), forbiddenResponse.getStatus());
        assertTrue(forbiddenResponse.getContentAsString().contains("You do not have permission"));
    }

    @Test
    void globalExceptionHandlerMapsIllegalArgumentsToBadRequest() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        Map<String, Object> body = handler.handleIllegalArgumentException(new IllegalArgumentException("Invalid input"))
                .getBody();

        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
        assertEquals("DOMAIN_VALIDATION_FAILED", body.get("code"));
        assertEquals("Invalid input", body.get("message"));
    }

    @Test
    void cacheConfigCreatesRedisCacheManager() {
        CacheManager cacheManager = ReflectionTestUtils.invokeMethod(
                new CacheConfig(),
                "cacheManager",
                mock(RedisConnectionFactory.class),
                Duration.ofHours(1)
        );

        assertNotNull(cacheManager);
    }

    private JwtCookieService jwtCookieService() {
        JwtCookieService service = new JwtCookieService();
        ReflectionTestUtils.setField(service, "accessTokenCookieName", "ACCESS_TOKEN");
        ReflectionTestUtils.setField(service, "refreshTokenCookieName", "REFRESH_TOKEN");
        ReflectionTestUtils.setField(service, "accessTokenExpirationMs", 900000L);
        ReflectionTestUtils.setField(service, "refreshTokenExpirationMs", 604800000L);
        ReflectionTestUtils.setField(service, "secure", false);
        ReflectionTestUtils.setField(service, "sameSite", "Lax");
        return service;
    }

    private User user() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User(
                null,
                "Owner",
                "owner@example.com",
                "Password1!",
                null,
                "USER",
                true,
                now,
                now
        );
        return user;
    }
}
