package com.zeki.demo.progress.controller;

import com.zeki.demo.progress.dto.ProgressInsightsResponse;
import com.zeki.demo.progress.dto.ProgressSummaryResponse;
import com.zeki.demo.progress.mapperDtoDomain.ProgressDtoDomain;
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

    @Operation(summary = "Get workout-driven summary for dashboard/progress")
    @ApiResponse(responseCode = "200", description = "Progress summary")
    @GetMapping("/summary")
    public ProgressSummaryResponse getProgressSummary(Authentication authentication) {
        return progressDtoDomain.toSummaryResponse(
                progressService.getProgressSummary(authentication.getName())
        );
    }

    @Operation(summary = "Get workout-driven insights for progress charts")
    @ApiResponse(responseCode = "200", description = "Progress insights")
    @GetMapping("/insights")
    public ProgressInsightsResponse getProgressInsights(Authentication authentication) {
        return progressDtoDomain.toInsightsResponse(
                progressService.getProgressInsights(authentication.getName())
        );
    }
}
