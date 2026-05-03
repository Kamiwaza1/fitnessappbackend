package com.zeki.demo.Library.dto;

import com.zeki.demo.Library.enums.LibraryCategory;
import java.time.LocalDate;
import java.util.UUID;

public record LibraryResponse(
        UUID libraryId,
        String libraryName,
        String description,
        LibraryCategory category,
        LocalDate updatedAt,
        LocalDate createdAt
) {
}
