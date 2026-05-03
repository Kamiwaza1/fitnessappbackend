package com.zeki.demo.Library.repository;

import com.zeki.demo.Library.domain.Library;
import com.zeki.demo.Library.entity.LibraryEntity;
import com.zeki.demo.Library.mapperEntityDomain.LibraryEntityMapper;
import com.zeki.demo.Library.repository.jpa.LibraryRepoJPA;
import com.zeki.demo.common.exception.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LibraryRepositoryImpl implements LibraryRepository {

    private final LibraryRepoJPA libraryRepoJPA;
    private final LibraryEntityMapper libraryEntityMapper;

    @Override
    public Optional<Library> getLibraryItemById(UUID libraryId) {
        return libraryRepoJPA.findById(libraryId).map(libraryEntityMapper::toDomain);
    }

    @Override
    public Library save(Library library) {
        LibraryEntity savedEntity = libraryRepoJPA.save(libraryEntityMapper.toEntity(library));
        return libraryEntityMapper.toDomain(savedEntity);
    }

    @Override
    public List<Library> findAll() {
        return libraryRepoJPA.findAll().stream().map(libraryEntityMapper::toDomain).toList();
    }

    @Override
    public void delete(UUID libraryId) {
        LibraryEntity entity = libraryRepoJPA.findById(libraryId)
                .orElseThrow(() -> new NotFoundException("Library item not found"));
        libraryRepoJPA.delete(entity);
    }
}
