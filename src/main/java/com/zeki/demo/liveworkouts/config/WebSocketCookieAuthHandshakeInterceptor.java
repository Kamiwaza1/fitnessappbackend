package com.zeki.demo.liveworkouts.config;

import com.zeki.demo.users.security.JWTService;
import com.zeki.demo.users.security.JwtCookieService;
import io.jsonwebtoken.JwtException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
@RequiredArgsConstructor
public class WebSocketCookieAuthHandshakeInterceptor implements HandshakeInterceptor {

    static final String AUTHENTICATION_ATTRIBUTE = "AUTHENTICATION";

    private final JwtCookieService jwtCookieService;
    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return false;
        }

        return jwtCookieService.getAccessToken(servletRequest.getServletRequest())
                .map(this::authenticate)
                .map(authentication -> {
                    attributes.put(AUTHENTICATION_ATTRIBUTE, authentication);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
    }

    private Authentication authenticate(String jwt) {
        try {
            String userEmail = jwtService.extractUsername(jwt);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            if (!jwtService.isTokenValid(jwt, userDetails)) {
                return null;
            }
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException ex) {
            return null;
        }
    }
}
