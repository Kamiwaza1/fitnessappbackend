package com.zeki.demo.exerciseLibrary.controller;

import com.zeki.demo.exerciseLibrary.dto.ExerciseLibraryItemResponse;
import com.zeki.demo.exerciseLibrary.dto.ExerciseLibraryListRequest;
import com.zeki.demo.exerciseLibrary.dto.ExerciseLibraryResponse;
import com.zeki.demo.exerciseLibrary.dto.ExerciseLibrarySearchRequest;
import com.zeki.demo.exerciseLibrary.dto.ExerciseMuscleResponse;
import com.zeki.demo.exerciseLibrary.dto.ExerciseReferenceResponse;
import com.zeki.demo.exerciseLibrary.service.ExerciseLibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/exercise-library")
@RequiredArgsConstructor
@Tag(name = "Exercise Library", description = "Endpoints for browsing external exercise library data")
public class ExerciseLibraryController {

    private final ExerciseLibraryService exerciseLibraryService;

    @Operation(summary = "Search exercises from the exercise library")
    @ApiResponse(responseCode = "200", description = "Exercise search results")
    @GetMapping("/exercises/search")
    public ExerciseLibraryResponse searchExercises(
            @Valid @ModelAttribute ExerciseLibrarySearchRequest request,
            Authentication authentication
    ) {
        return exerciseLibraryService.searchExercises(request);
    }

    @Operation(summary = "Get an exercise by ID from the exercise library")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exercise details"),
            @ApiResponse(responseCode = "404", description = "Exercise not found")
    })
    @GetMapping("/exercises/{exerciseId}")
    public ExerciseLibraryItemResponse getExerciseById(
            @PathVariable Integer exerciseId,
            Authentication authentication
    ) {
        return exerciseLibraryService.getExerciseById(exerciseId);
    }

    @Operation(summary = "Get exercise muscles")
    @ApiResponse(responseCode = "200", description = "Exercise muscle list")
    @GetMapping("/muscles")
    public List<ExerciseMuscleResponse> getMuscles(
            @Valid @ModelAttribute ExerciseLibraryListRequest request,
            Authentication authentication
    ) {
        return exerciseLibraryService.getMuscles(request.limit(), request.offset());
    }

    @Operation(summary = "Get exercise equipment")
    @ApiResponse(responseCode = "200", description = "Exercise equipment list")
    @GetMapping("/equipment")
    public List<ExerciseReferenceResponse> getEquipment(
            @Valid @ModelAttribute ExerciseLibraryListRequest request,
            Authentication authentication
    ) {
        return exerciseLibraryService.getEquipment(request.limit(), request.offset());
    }

    @Operation(summary = "Get exercise categories")
    @ApiResponse(responseCode = "200", description = "Exercise category list")
    @GetMapping("/categories")
    public List<ExerciseReferenceResponse> getCategories(
            @Valid @ModelAttribute ExerciseLibraryListRequest request,
            Authentication authentication
    ) {
        return exerciseLibraryService.getCategories(request.limit(), request.offset());
    }
}
