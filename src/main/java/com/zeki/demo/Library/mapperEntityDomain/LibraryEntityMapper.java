package com.zeki.demo.Library.mapperEntityDomain;

import com.zeki.demo.Library.domain.Library;
import com.zeki.demo.Library.entity.LibraryEntity;
import org.springframework.stereotype.Component;

@Component
public class LibraryEntityMapper {

    public LibraryEntity toEntity(Library library) {
        return LibraryEntity.builder()
                .libraryId(library.getLibraryId())
                .libraryName(library.getLibraryName())
                .description(library.getDescription())
                .category(library.getCategory())
                .createdAt(library.getCreatedAt())
                .updatedAt(library.getUpdatedAt())
                .build();
    }

    public Library toDomain(LibraryEntity entity) {
        return new Library(
                entity.getLibraryName(),
                entity.getLibraryId(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
