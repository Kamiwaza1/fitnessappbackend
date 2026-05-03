package com.zeki.demo.Library.controller;

import com.zeki.demo.Library.dto.CreateLibraryRequest;
import com.zeki.demo.Library.dto.LibraryResponse;
import com.zeki.demo.Library.dto.UpdateLibraryRequest;
import com.zeki.demo.Library.mapperDtoDomain.LibraryDtoDomain;
import com.zeki.demo.Library.service.LibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/library")
@RequiredArgsConstructor
@Tag(name = "Library", description = "Endpoints for managing library items")
public class LibraryController {

    private final LibraryService libraryService;
    private final LibraryDtoDomain libraryDtoDomain;

    @Operation(summary = "Get all library items")
    @ApiResponse(responseCode = "200", description = "List of library items")
    @GetMapping
    public List<LibraryResponse> getAllLibraryItems() {
        return libraryService.getAllLibraryItems()
                .stream()
                .map(libraryDtoDomain::toResponse)
                .toList();
    }

    @Operation(summary = "Get a library item by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Library item details"),
            @ApiResponse(responseCode = "404", description = "Library item not found")
    })
    @GetMapping("/{libraryId}")
    public LibraryResponse getLibraryItemById(@PathVariable UUID libraryId) {
        return libraryDtoDomain.toResponse(libraryService.getLibraryItemById(libraryId));
    }

    @Operation(summary = "Create a new library item")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Library item created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LibraryResponse createLibraryItem(@Valid @RequestBody CreateLibraryRequest request) {
        return libraryDtoDomain.toResponse(libraryService.createLibraryItem(libraryDtoDomain.toDomain(request)));
    }

    @Operation(summary = "Update an existing library item")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Library item updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Library item not found")
    })
    @PatchMapping("/{libraryId}")
    public LibraryResponse updateLibraryItem(
            @PathVariable UUID libraryId,
            @Valid @RequestBody UpdateLibraryRequest request
    ) {
        return libraryDtoDomain.toResponse(libraryService.updateLibraryItem(libraryId, request));
    }

    @Operation(summary = "Delete an existing library item")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Library item deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Library item not found")
    })
    @DeleteMapping("/{libraryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLibraryItem(@PathVariable UUID libraryId) {
        libraryService.deleteLibraryItem(libraryId);
    }
}
