package com.zeki.demo.Library.repository.jpa;

import com.zeki.demo.Library.entity.LibraryEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryRepoJPA extends JpaRepository<LibraryEntity, UUID> {
}
