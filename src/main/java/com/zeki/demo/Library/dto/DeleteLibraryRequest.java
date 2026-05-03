package com.zeki.demo.Library.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteLibraryRequest {

    @NotNull
    private UUID libraryId;

    public DeleteLibraryRequest(UUID libraryId) {
        this.libraryId = libraryId;
    }
}
