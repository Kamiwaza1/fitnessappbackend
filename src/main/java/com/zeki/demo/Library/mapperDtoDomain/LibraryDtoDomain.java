package com.zeki.demo.Library.mapperDtoDomain;

import com.zeki.demo.Library.domain.Library;
import com.zeki.demo.Library.dto.CreateLibraryRequest;
import com.zeki.demo.Library.dto.LibraryResponse;
import com.zeki.demo.Library.dto.UpdateLibraryRequest;
import com.zeki.demo.Library.enums.LibraryCategory;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class LibraryDtoDomain {

    private static final LibraryCategory DEFAULT_CATEGORY = LibraryCategory.GENERAL;

    public Library toDomain(CreateLibraryRequest request) {
        LocalDate now = LocalDate.now();
        return new Library(
                request.libraryName(),
                request.libraryId(),
                request.description(),
                request.category() != null ? request.category() : DEFAULT_CATEGORY,
                now,
                now
        );
    }

    public Library toUpdatedDomain(UpdateLibraryRequest request, Library existingLibrary) {
        return new Library(
                request.libraryName() != null ? request.libraryName() : existingLibrary.getLibraryName(),
                existingLibrary.getLibraryId(),
                request.description() != null ? request.description() : existingLibrary.getDescription(),
                request.category() != null ? request.category() : existingLibrary.getCategory(),
                existingLibrary.getCreatedAt(),
                LocalDate.now()
        );
    }

    public LibraryResponse toResponse(Library library) {
        return new LibraryResponse(
                library.getLibraryId(),
                library.getLibraryName(),
                library.getDescription(),
                library.getCategory(),
                library.getUpdatedAt(),
                library.getCreatedAt()
        );
    }
}
