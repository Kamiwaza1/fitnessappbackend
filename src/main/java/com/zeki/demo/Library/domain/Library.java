package com.zeki.demo.Library.domain;

import com.zeki.demo.Library.enums.LibraryCategory;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Library {
    private final UUID libraryId;
    private final String libraryName;
    private final String description;
    private final LibraryCategory category;
    private final LocalDate createdAt;
    private final LocalDate updatedAt;

    public Library(
            String libraryName,
            UUID libraryId,
            String description,
            LibraryCategory category,
            LocalDate createdAt,
            LocalDate updatedAt
    ) {
        this.libraryId = libraryId;
        this.libraryName = libraryName;
        this.description = description;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
