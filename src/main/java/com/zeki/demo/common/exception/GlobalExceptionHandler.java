package com.zeki.demo.common.exception;

import com.zeki.demo.exerciselibrary.exception.ExerciseLibraryValidationException;
import com.zeki.demo.liveworkouts.exception.LiveWorkoutAccessException;
import com.zeki.demo.liveworkouts.exception.LiveWorkoutValidationException;
import com.zeki.demo.nutrition.exception.FoodProductValidationException;
import com.zeki.demo.nutrition.exception.NutritionNotFoundException;
import com.zeki.demo.nutrition.exception.NutritionValidationException;
import com.zeki.demo.users.exception.AuthenticationValidationException;
import com.zeki.demo.users.exception.PasswordResetException;
import com.zeki.demo.users.exception.UserValidationException;
import com.zeki.demo.workouts.exception.WorkoutNotFoundException;
import com.zeki.demo.workouts.exception.WorkoutValidationException;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        return error(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(ConflictException ex) {
        return error(HttpStatus.CONFLICT, "CONFLICT", ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedException ex) {
        return error(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(WorkoutNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleWorkoutNotFound(WorkoutNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, "WORKOUT_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(NutritionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNutritionNotFound(NutritionNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, "NUTRITION_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(WorkoutValidationException.class)
    public ResponseEntity<Map<String, Object>> handleWorkoutValidation(WorkoutValidationException ex) {
        return error(HttpStatus.BAD_REQUEST, "WORKOUT_VALIDATION_FAILED", ex.getMessage());
    }

    @ExceptionHandler(NutritionValidationException.class)
    public ResponseEntity<Map<String, Object>> handleNutritionValidation(NutritionValidationException ex) {
        return error(HttpStatus.BAD_REQUEST, "NUTRITION_VALIDATION_FAILED", ex.getMessage());
    }

    @ExceptionHandler(ExerciseLibraryValidationException.class)
    public ResponseEntity<Map<String, Object>> handleExerciseLibraryValidation(ExerciseLibraryValidationException ex) {
        return error(HttpStatus.BAD_REQUEST, "EXERCISE_LIBRARY_VALIDATION_FAILED", ex.getMessage());
    }

    @ExceptionHandler(FoodProductValidationException.class)
    public ResponseEntity<Map<String, Object>> handleFoodProductValidation(FoodProductValidationException ex) {
        return error(HttpStatus.BAD_REQUEST, "FOOD_PRODUCT_VALIDATION_FAILED", ex.getMessage());
    }

    @ExceptionHandler(LiveWorkoutValidationException.class)
    public ResponseEntity<Map<String, Object>> handleLiveWorkoutValidation(LiveWorkoutValidationException ex) {
        return error(HttpStatus.BAD_REQUEST, "LIVE_WORKOUT_VALIDATION_FAILED", ex.getMessage());
    }

    @ExceptionHandler(LiveWorkoutAccessException.class)
    public ResponseEntity<Map<String, Object>> handleLiveWorkoutAccess(LiveWorkoutAccessException ex) {
        return error(HttpStatus.UNAUTHORIZED, "LIVE_WORKOUT_ACCESS_DENIED", ex.getMessage());
    }

    @ExceptionHandler(AuthenticationValidationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationValidation(AuthenticationValidationException ex) {
        return error(HttpStatus.BAD_REQUEST, "AUTH_VALIDATION_FAILED", ex.getMessage());
    }

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<Map<String, Object>> handleUserValidation(UserValidationException ex) {
        return error(HttpStatus.BAD_REQUEST, "USER_VALIDATION_FAILED", ex.getMessage());
    }

    @ExceptionHandler(PasswordResetException.class)
    public ResponseEntity<Map<String, Object>> handlePasswordReset(PasswordResetException ex) {
        return error(HttpStatus.BAD_REQUEST, "PASSWORD_RESET_FAILED", ex.getMessage());
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<Map<String, Object>> handleExternalService(ExternalServiceException ex) {
        return error(HttpStatus.BAD_GATEWAY, "EXTERNAL_SERVICE_ERROR", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage())
        );

        return validationError(fieldErrors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                fieldErrors.putIfAbsent(lastPathNode(violation.getPropertyPath().toString()), violation.getMessage())
        );

        return validationError(fieldErrors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return error(HttpStatus.BAD_REQUEST, "DOMAIN_VALIDATION_FAILED", ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        return error(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST_BODY", "Request body is missing or malformed.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String parameter = ex.getName();
        return error(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", parameter + " has an invalid value.");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParameter(MissingServletRequestParameterException ex) {
        return error(HttpStatus.BAD_REQUEST, "MISSING_PARAMETER", ex.getParameterName() + " is required.");
    }

    private ResponseEntity<Map<String, Object>> validationError(Map<String, String> fieldErrors) {
        Map<String, Object> body = baseBody(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_FAILED",
                "Validation failed",
                "Please correct the highlighted fields and try again."
        );
        body.put("fieldErrors", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(baseBody(status, code, status.getReasonPhrase(), message));
    }

    private Map<String, Object> baseBody(HttpStatus status, String code, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", error);
        body.put("code", code);
        body.put("message", message);
        return body;
    }

    private String lastPathNode(String propertyPath) {
        int dotIndex = propertyPath.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == propertyPath.length() - 1) {
            return propertyPath;
        }
        return propertyPath.substring(dotIndex + 1);
    }
}
