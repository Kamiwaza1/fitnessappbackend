package com.zeki.demo.Library.dto;

import com.zeki.demo.Library.enums.LibraryCategory;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record CreateLibraryRequest(
        @NotBlank(message = "Library name is required")
        String libraryName,
        String description,
        LibraryCategory category,
        UUID libraryId
) {
}
