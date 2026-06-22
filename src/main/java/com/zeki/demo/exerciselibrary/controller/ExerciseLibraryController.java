package com.zeki.demo.exerciselibrary.controller;

import com.zeki.demo.exerciselibrary.dto.ExerciseLibraryItemResponse;
import com.zeki.demo.exerciselibrary.dto.ExerciseLibraryListRequest;
import com.zeki.demo.exerciselibrary.dto.ExerciseLibraryResponse;
import com.zeki.demo.exerciselibrary.dto.ExerciseLibrarySearchRequest;
import com.zeki.demo.exerciselibrary.dto.ExerciseMuscleResponse;
import com.zeki.demo.exerciselibrary.dto.ExerciseReferenceResponse;
import com.zeki.demo.exerciselibrary.mapperdtodomain.ExerciseLibraryDtoDomain;
import com.zeki.demo.exerciselibrary.service.ExerciseLibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/exercise-library")
@RequiredArgsConstructor
@Tag(name = "Exercise Library", description = "Endpoints for browsing external exercise library data")
@Validated
public class ExerciseLibraryController {

    private final ExerciseLibraryService exerciseLibraryService;
    private final ExerciseLibraryDtoDomain exerciseLibraryDtoDomain;

    @Operation(summary = "Search exercises from the exercise library")
    @ApiResponse(responseCode = "200", description = "Exercise search results")
    @GetMapping("/exercises/search")
    public ExerciseLibraryResponse searchExercises(
            @Valid @ModelAttribute ExerciseLibrarySearchRequest request,
            Authentication authentication
    ) {
        return exerciseLibraryDtoDomain.toResponse(
                exerciseLibraryService.searchExercises(exerciseLibraryDtoDomain.toDomain(request))
        );
    }

    @Operation(summary = "Get an exercise by ID from the exercise library")
 
    @GetMapping("/exercises/{exerciseId}")
    public ExerciseLibraryItemResponse getExerciseById(
            @PathVariable @Min(value = 1, message = "Exercise ID must be at least 1") Integer exerciseId,
            Authentication authentication
    ) {
        return exerciseLibraryDtoDomain.toResponse(exerciseLibraryService.getExerciseById(exerciseId));
    }

    @Operation(summary = "Get exercise muscles")
    @ApiResponse(responseCode = "200", description = "Exercise muscle list")
    @GetMapping("/muscles")
    public List<ExerciseMuscleResponse> getMuscles(
            @Valid @ModelAttribute ExerciseLibraryListRequest request,
            Authentication authentication
    ) {
        return exerciseLibraryDtoDomain.toMuscleResponses(
                exerciseLibraryService.getMuscles(exerciseLibraryDtoDomain.toDomain(request))
        );
    }

    @Operation(summary = "Get exercise equipment")
    @ApiResponse(responseCode = "200", description = "Exercise equipment list")
    @GetMapping("/equipment")
    public List<ExerciseReferenceResponse> getEquipment(
            @Valid @ModelAttribute ExerciseLibraryListRequest request,
            Authentication authentication
    ) {
        return exerciseLibraryDtoDomain.toReferenceResponses(
                exerciseLibraryService.getEquipment(exerciseLibraryDtoDomain.toDomain(request))
        );
    }

    @Operation(summary = "Get exercise categories")
    @ApiResponse(responseCode = "200", description = "Exercise category list")
    @GetMapping("/categories")
    public List<ExerciseReferenceResponse> getCategories(
            @Valid @ModelAttribute ExerciseLibraryListRequest request,
            Authentication authentication
    ) {
        return exerciseLibraryDtoDomain.toReferenceResponses(
                exerciseLibraryService.getCategories(exerciseLibraryDtoDomain.toDomain(request))
        );
    }
}
