package com.zeki.demo.unit;

import com.zeki.demo.liveworkouts.config.WebSocketAuthChannelInterceptor;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WebSocketAuthChannelInterceptorTest {

    private static final String EMAIL = "user@example.com";

    private WebSocketAuthChannelInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new WebSocketAuthChannelInterceptor();
    }

    @Test
    void connectWithoutSessionAuthenticationIsRejected() {
        Message<byte[]> message = connectMessage(null);

        assertThrows(BadCredentialsException.class, () -> interceptor.preSend(message, null));
    }

    @Test
    void connectWithSessionAuthenticationSetsPrincipal() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                EMAIL,
                null,
                AuthorityUtils.createAuthorityList("ROLE_USER")
        );
        Message<byte[]> message = connectMessage(authentication);

        Message<?> result = interceptor.preSend(message, null);

        assertNotNull(result.getHeaders().get("simpUser"));
        assertEquals(EMAIL, result.getHeaders().get("simpUser", java.security.Principal.class).getName());
    }

    private Message<byte[]> connectMessage(Authentication authentication) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setLeaveMutable(true);
        if (authentication != null) {
            accessor.setSessionAttributes(Map.of("AUTHENTICATION", authentication));
        }
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }
}
