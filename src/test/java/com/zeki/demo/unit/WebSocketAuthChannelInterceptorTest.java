package com.zeki.demo.unit;

import com.zeki.demo.liveworkouts.config.WebSocketAuthChannelInterceptor;
import com.zeki.demo.users.security.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSocketAuthChannelInterceptorTest {

    private static final String TOKEN = "token";
    private static final String EMAIL = "user@example.com";

    @Mock
    private JWTService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    private WebSocketAuthChannelInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new WebSocketAuthChannelInterceptor(jwtService, userDetailsService);
    }

    @Test
    void connectWithoutJwtIsRejected() {
        Message<byte[]> message = connectMessage(null);

        assertThrows(BadCredentialsException.class, () -> interceptor.preSend(message, null));
    }

    @Test
    void connectWithValidJwtSetsPrincipal() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(EMAIL)
                .password("password")
                .authorities("ROLE_USER")
                .build();
        when(jwtService.extractUsername(TOKEN)).thenReturn(EMAIL);
        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);
        when(jwtService.isTokenValid(TOKEN, userDetails)).thenReturn(true);

        Message<byte[]> message = connectMessage("Bearer " + TOKEN);

        Message<?> result = interceptor.preSend(message, null);

        assertNotNull(result.getHeaders().get("simpUser"));
        assertEquals(EMAIL, result.getHeaders().get("simpUser", java.security.Principal.class).getName());
    }

    private Message<byte[]> connectMessage(String authorizationHeader) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setLeaveMutable(true);
        if (authorizationHeader != null) {
            accessor.setNativeHeader("Authorization", authorizationHeader);
        }
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }
}
