package com.zeki.demo.Library.dto;

import com.zeki.demo.Library.enums.LibraryCategory;
import java.util.UUID;

public record UpdateLibraryRequest(
        UUID libraryId,
        String libraryName,
        String description,
        LibraryCategory category
) {
}
