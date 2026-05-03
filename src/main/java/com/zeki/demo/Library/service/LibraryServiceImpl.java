package com.zeki.demo.Library.service;

import com.zeki.demo.Library.LibraryException.LibraryExistException;
import com.zeki.demo.Library.domain.Library;
import com.zeki.demo.Library.dto.UpdateLibraryRequest;
import com.zeki.demo.Library.mapperDtoDomain.LibraryDtoDomain;
import com.zeki.demo.Library.repository.LibraryRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class LibraryServiceImpl implements LibraryService {

    private final LibraryRepository libraryRepository;
    private final LibraryDtoDomain libraryDtoDomain;

    @Override
    public Library createLibraryItem(Library library) {
        return libraryRepository.save(library);
    }

    @Override
    public Library getLibraryItemById(UUID libraryId) {
        return libraryRepository.getLibraryItemById(libraryId)
                .orElseThrow(() -> new LibraryExistException("Library item not found"));
    }

    @Override
    public List<Library> getAllLibraryItems() {
        return libraryRepository.findAll();
    }

    @Override
    public Library updateLibraryItem(UUID libraryId, UpdateLibraryRequest request) {
        Library existingLibrary = libraryRepository.getLibraryItemById(libraryId)
                .orElseThrow(() -> new LibraryExistException("Library item not found"));
        return libraryRepository.save(libraryDtoDomain.toUpdatedDomain(request, existingLibrary));
    }

    @Override
    public void deleteLibraryItem(UUID libraryId) {
        libraryRepository.delete(libraryId);
    }
}
