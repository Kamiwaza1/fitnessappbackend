package com.zeki.demo.users.controller;

import com.zeki.demo.users.dto.UpdateUserRequest;
import com.zeki.demo.users.dto.UserResponse;
import com.zeki.demo.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "user", description = "User endpoints")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get current user", description = "Returns the authenticated user's profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUser(authentication.getName()));
    }

    @Operation(summary = "Update current user", description = "Updates the authenticated user's profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateUser(
            @Valid @RequestBody UpdateUserRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(userService.updateUser(authentication.getName(), request));
    }

    @Operation(summary = "Delete current user", description = "Soft deletes the authenticated user's account")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentUser(Authentication authentication) {
        userService.deleteCurrentUser(authentication.getName());
    }
}
