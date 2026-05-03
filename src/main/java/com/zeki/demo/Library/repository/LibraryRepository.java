package com.zeki.demo.Library.repository;

import com.zeki.demo.Library.domain.Library;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LibraryRepository {

    Optional<Library> getLibraryItemById(UUID libraryId);

    Library save(Library library);

    List<Library> findAll();

    void delete(UUID libraryId);
}
