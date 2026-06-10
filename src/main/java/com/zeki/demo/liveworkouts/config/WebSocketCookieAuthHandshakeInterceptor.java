package com.zeki.demo.liveworkouts.config;

import com.zeki.demo.users.security.JwtCookieService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
@RequiredArgsConstructor
public class WebSocketCookieAuthHandshakeInterceptor implements HandshakeInterceptor {

    static final String ACCESS_TOKEN_ATTRIBUTE = "ACCESS_TOKEN";

    private final JwtCookieService jwtCookieService;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            jwtCookieService.getAccessToken(servletRequest.getServletRequest())
                    .ifPresent(token -> attributes.put(ACCESS_TOKEN_ATTRIBUTE, token));
        }
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
    }
}
