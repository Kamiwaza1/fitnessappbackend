package com.zeki.demo.progress.controller;

import com.zeki.demo.progress.dto.ProgressResponse;
import com.zeki.demo.progress.mapperdtodomain.ProgressDtoDomain;
import com.zeki.demo.progress.service.ProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
@Tag(name = "Progress", description = "Workout-driven progress endpoints")
public class ProgressController {

    private final ProgressService progressService;
    private final ProgressDtoDomain progressDtoDomain;

    @Operation(summary = "Get workout-driven progress")
    @ApiResponse(responseCode = "200", description = "Progress data for dashboard and charts")
    @GetMapping
    public ProgressResponse getProgress(Authentication authentication) {
        return progressDtoDomain.toResponse(
                progressService.getProgress(authentication.getName())
        );
    }
}
