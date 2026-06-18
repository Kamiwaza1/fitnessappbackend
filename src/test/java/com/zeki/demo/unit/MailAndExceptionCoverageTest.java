package com.zeki.demo.unit;

import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.common.exception.ConflictException;
import com.zeki.demo.common.exception.ExternalServiceException;
import com.zeki.demo.common.exception.GlobalExceptionHandler;
import com.zeki.demo.common.exception.NotFoundException;
import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.mail.EmailTemplateRenderer;
import com.zeki.demo.mail.ResendEmailService;
import com.zeki.demo.users.domain.User;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MailAndExceptionCoverageTest {

    @Test
    void emailTemplateRendererEscapesUserControlledValues() {
        EmailTemplateRenderer renderer = new EmailTemplateRenderer();
        User user = user("<Zeki & Co>");

        String welcome = renderer.welcome(user);
        String reset = renderer.passwordReset(
                user,
                "https://darklift.test/reset?token=<abc>&next=/home",
                "12<456",
                30
        );
        String deletion = renderer.accountDeletion(
                user,
                "https://darklift.test/login?email=<user>",
                "http://localhost:3000/login?email=<user>",
                14
        );

        assertTrue(welcome.contains("&lt;Zeki &amp; Co&gt;"));
        assertTrue(reset.contains("&lt;Zeki &amp; Co&gt;"));
        assertTrue(reset.contains("12&lt;456"));
        assertTrue(reset.contains("30"));
        assertTrue(deletion.contains("&lt;Zeki &amp; Co&gt;"));
        assertTrue(deletion.contains("14"));
        assertTrue(deletion.contains("&lt;user&gt;"));
    }

    @Test
    void resendEmailServiceSkipsAllMailTypesWhenDisabled() {
        EmailTemplateRenderer renderer = new EmailTemplateRenderer();
        ResendEmailService service = new ResendEmailService(
                WebClient.builder(),
                "http://localhost:1",
                "",
                "DarkLift <noreply@darklift.test>",
                false,
                true,
                renderer
        );
        User user = user("Zeki");

        service.sendWelcomeEmail(user);
        service.sendPasswordResetEmail(user, "https://darklift.test/reset", "123456", 30);
        service.sendAccountDeletionEmail(user, "https://darklift.test/login", "http://localhost:3000/login", 14);
    }

    @Test
    void globalExceptionHandlerBuildsExpectedResponses() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        assertSimpleError(handler.handleBadRequest(new BadRequestException("bad")), HttpStatus.BAD_REQUEST, "bad");
        assertSimpleError(handler.handleConflict(new ConflictException("conflict")), HttpStatus.CONFLICT, "conflict");
        assertSimpleError(handler.handleUnauthorized(new UnauthorizedException("unauthorized")), HttpStatus.UNAUTHORIZED, "unauthorized");
        assertSimpleError(handler.handleNotFound(new NotFoundException("missing")), HttpStatus.NOT_FOUND, "missing");
        assertSimpleError(handler.handleExternalService(new ExternalServiceException("external")), HttpStatus.BAD_GATEWAY, "external");

        ResponseEntity<Map<String, Object>> malformed = handler.handleMessageNotReadableException(
                new HttpMessageNotReadableException("invalid", null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, malformed.getStatusCode());
        assertNotNull(malformed.getBody());
        assertEquals(400, malformed.getBody().get("status"));
        assertEquals("Invalid request body", malformed.getBody().get("error"));
        assertEquals("Request body is missing or malformed.", malformed.getBody().get("message"));
    }

    private void assertSimpleError(
            ResponseEntity<Map<String, Object>> response,
            HttpStatus status,
            String message
    ) {
        assertEquals(status, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(status.value(), response.getBody().get("status"));
        assertEquals(message, response.getBody().get("error"));
    }

    private User user(String fullName) {
        return new User(
                UUID.randomUUID(),
                fullName,
                "zeki@example.com",
                "StrongPassword123!",
                2200,
                "USER",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
