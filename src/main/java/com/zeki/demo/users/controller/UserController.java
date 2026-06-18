package com.zeki.demo.users.controller;

import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.users.dto.AccountDeletionRequest;
import com.zeki.demo.users.dto.UpdateUserRequest;
import com.zeki.demo.users.dto.UserResponse;
import com.zeki.demo.users.mapperdtodomain.UserDtoDomain;
import com.zeki.demo.users.security.JwtCookieService;
import com.zeki.demo.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "user", description = "User endpoints")
public class UserController {

    private final UserService userService;
    private final UserDtoDomain userDtoDomain;
    private final JwtCookieService jwtCookieService;

    @Operation(summary = "Get current user", description = "Returns the authenticated user's profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {
        return userDtoDomain.toResponse(userService.getCurrentUser(authentication.getName()));
    }

    @Operation(summary = "Update current user", description = "Updates the authenticated user's profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PatchMapping("/me")
    public UserResponse updateUser(
            @Valid @RequestBody UpdateUserRequest updateUserRequest,
            Authentication authentication
    ) {
        return userDtoDomain.toResponse(userService.updateUser(authentication.getName(), updateUserRequest));
    }

    @Operation(summary = "Delete current user", description = "Schedules the authenticated user's account for deletion after a 7 day recovery window")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deletion scheduled successfully"),
            @ApiResponse(responseCode = "400", description = "Password is required"),
            @ApiResponse(responseCode = "401", description = "Not authenticated or password is invalid")
    })
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentUser(
            @Valid @RequestBody AccountDeletionRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        requireAccessToken(httpRequest);
        userService.deleteCurrentUser(authentication.getName(), request.password());
    }

    @Operation(summary = "Cancel account deletion", description = "Recovers an account during the 7 day deletion grace period")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account deletion canceled"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping("/me/deletion/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelDeletion(Authentication authentication, HttpServletRequest httpRequest) {
        requireAccessToken(httpRequest);
        userService.cancelDeletion(authentication.getName());
    }

    private void requireAccessToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ") && !authHeader.substring(7).isBlank()) {
            return;
        }
        if (jwtCookieService.getAccessToken(request).isPresent()) {
            return;
        }
        throw new UnauthorizedException("Access token is required");
    }
}
