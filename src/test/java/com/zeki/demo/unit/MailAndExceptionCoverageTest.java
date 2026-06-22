package com.zeki.demo.unit;

import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.common.exception.ConflictException;
import com.zeki.demo.common.exception.ExternalServiceException;
import com.zeki.demo.common.exception.GlobalExceptionHandler;
import com.zeki.demo.common.exception.NotFoundException;
import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.exerciselibrary.exception.ExerciseLibraryValidationException;
import com.zeki.demo.liveworkouts.exception.LiveWorkoutAccessException;
import com.zeki.demo.liveworkouts.exception.LiveWorkoutValidationException;
import com.zeki.demo.mail.EmailTemplateRenderer;
import com.zeki.demo.mail.ResendEmailService;
import com.zeki.demo.nutrition.exception.FoodProductValidationException;
import com.zeki.demo.nutrition.exception.NutritionNotFoundException;
import com.zeki.demo.nutrition.exception.NutritionValidationException;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.exception.AuthenticationValidationException;
import com.zeki.demo.users.exception.PasswordResetException;
import com.zeki.demo.users.exception.UserValidationException;
import com.zeki.demo.workouts.exception.WorkoutNotFoundException;
import com.zeki.demo.workouts.exception.WorkoutValidationException;
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

        assertError(handler.handleBadRequest(new BadRequestException("bad")), HttpStatus.BAD_REQUEST, "BAD_REQUEST", "bad");
        assertError(handler.handleConflict(new ConflictException("conflict")), HttpStatus.CONFLICT, "CONFLICT", "conflict");
        assertError(handler.handleUnauthorized(new UnauthorizedException("unauthorized")), HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "unauthorized");
        assertError(handler.handleNotFound(new NotFoundException("missing")), HttpStatus.NOT_FOUND, "NOT_FOUND", "missing");
        assertError(handler.handleWorkoutNotFound(new WorkoutNotFoundException("missing workout")), HttpStatus.NOT_FOUND, "WORKOUT_NOT_FOUND", "missing workout");
        assertError(handler.handleNutritionNotFound(new NutritionNotFoundException("missing nutrition")), HttpStatus.NOT_FOUND, "NUTRITION_NOT_FOUND", "missing nutrition");
        assertError(handler.handleWorkoutValidation(new WorkoutValidationException("bad workout")), HttpStatus.BAD_REQUEST, "WORKOUT_VALIDATION_FAILED", "bad workout");
        assertError(handler.handleNutritionValidation(new NutritionValidationException("bad nutrition")), HttpStatus.BAD_REQUEST, "NUTRITION_VALIDATION_FAILED", "bad nutrition");
        assertError(handler.handleExerciseLibraryValidation(new ExerciseLibraryValidationException("bad exercise library")), HttpStatus.BAD_REQUEST, "EXERCISE_LIBRARY_VALIDATION_FAILED", "bad exercise library");
        assertError(handler.handleFoodProductValidation(new FoodProductValidationException("bad food product")), HttpStatus.BAD_REQUEST, "FOOD_PRODUCT_VALIDATION_FAILED", "bad food product");
        assertError(handler.handleLiveWorkoutValidation(new LiveWorkoutValidationException("bad live workout")), HttpStatus.BAD_REQUEST, "LIVE_WORKOUT_VALIDATION_FAILED", "bad live workout");
        assertError(handler.handleLiveWorkoutAccess(new LiveWorkoutAccessException("join first")), HttpStatus.UNAUTHORIZED, "LIVE_WORKOUT_ACCESS_DENIED", "join first");
        assertError(handler.handleAuthenticationValidation(new AuthenticationValidationException("bad auth")), HttpStatus.BAD_REQUEST, "AUTH_VALIDATION_FAILED", "bad auth");
        assertError(handler.handleUserValidation(new UserValidationException("bad user")), HttpStatus.BAD_REQUEST, "USER_VALIDATION_FAILED", "bad user");
        assertError(handler.handlePasswordReset(new PasswordResetException("bad reset")), HttpStatus.BAD_REQUEST, "PASSWORD_RESET_FAILED", "bad reset");
        assertError(handler.handleExternalService(new ExternalServiceException("external")), HttpStatus.BAD_GATEWAY, "EXTERNAL_SERVICE_ERROR", "external");

        ResponseEntity<Map<String, Object>> malformed = handler.handleMessageNotReadableException(
                new HttpMessageNotReadableException("invalid", null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, malformed.getStatusCode());
        assertNotNull(malformed.getBody());
        assertEquals(400, malformed.getBody().get("status"));
        assertEquals("MALFORMED_REQUEST_BODY", malformed.getBody().get("code"));
        assertEquals("Request body is missing or malformed.", malformed.getBody().get("message"));
    }

    private void assertError(
            ResponseEntity<Map<String, Object>> response,
            HttpStatus status,
            String code,
            String message
    ) {
        assertEquals(status, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(status.value(), response.getBody().get("status"));
        assertEquals(code, response.getBody().get("code"));
        assertEquals(message, response.getBody().get("message"));
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
