package com.zeki.demo.liveworkouts.config;

import java.util.Map;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (!isConnectCommand(accessor)) {
            return message;
        }

        accessor.setUser(getRequiredAuthentication(accessor));
        return message;
    }

    private boolean isConnectCommand(StompHeaderAccessor accessor) {
        return accessor != null && StompCommand.CONNECT.equals(accessor.getCommand());
    }

    private Authentication getRequiredAuthentication(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes == null) {
            throw new BadCredentialsException("Missing WebSocket authentication");
        }

        Object authentication = sessionAttributes.get(WebSocketCookieAuthHandshakeInterceptor.AUTHENTICATION_ATTRIBUTE);
        if (authentication instanceof Authentication auth && auth.isAuthenticated()) {
            return auth;
        }
        throw new BadCredentialsException("Missing WebSocket authentication");
    }
}
