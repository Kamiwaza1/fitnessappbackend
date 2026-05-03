package com.zeki.demo.Library.service;

import com.zeki.demo.Library.domain.Library;
import com.zeki.demo.Library.dto.UpdateLibraryRequest;
import java.util.List;
import java.util.UUID;

public interface LibraryService {

    Library createLibraryItem(Library library);

    Library getLibraryItemById(UUID libraryId);

    List<Library> getAllLibraryItems();

    Library updateLibraryItem(UUID libraryId, UpdateLibraryRequest request);

    void deleteLibraryItem(UUID libraryId);
}
