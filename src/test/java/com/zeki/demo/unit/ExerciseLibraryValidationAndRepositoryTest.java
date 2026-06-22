package com.zeki.demo.unit;

import com.zeki.demo.exerciselibrary.client.WgerExerciseClient;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibrary;
import com.zeki.demo.exerciselibrary.exception.ExerciseLibraryValidationException;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibraryListCriteria;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibrarySearchCriteria;
import com.zeki.demo.exerciselibrary.entity.ExerciseLibraryEntity;
import com.zeki.demo.exerciselibrary.entity.ExerciseLibraryItemEntity;
import com.zeki.demo.exerciselibrary.entity.ExerciseMuscleEntity;
import com.zeki.demo.exerciselibrary.entity.ExerciseReferenceEntity;
import com.zeki.demo.exerciselibrary.mapperentitydomain.ExerciseLibraryEntityMapper;
import com.zeki.demo.exerciselibrary.repository.ExerciseLibraryRepositoryImpl;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExerciseLibraryValidationAndRepositoryTest {

    private final WgerExerciseClient wgerExerciseClient = mock(WgerExerciseClient.class);
    private final ExerciseLibraryRepositoryImpl repository = new ExerciseLibraryRepositoryImpl(
            wgerExerciseClient,
            new ExerciseLibraryEntityMapper()
    );

    @Test
    void searchCriteriaNormalizesBlankQueryAndTrimsText() {
        ExerciseLibrarySearchCriteria blankQuery = new ExerciseLibrarySearchCriteria("   ", 20, 0, 1, 2, 3);
        ExerciseLibrarySearchCriteria trimmedQuery = new ExerciseLibrarySearchCriteria("  push  ", null, null, null, null, null);

        assertNull(blankQuery.q());
        assertEquals("push", trimmedQuery.q());
    }

    @Test
    void searchCriteriaRejectsInvalidValues() {
        assertThrows(ExerciseLibraryValidationException.class, () -> new ExerciseLibrarySearchCriteria("a".repeat(101), null, null, null, null, null));
        assertThrows(ExerciseLibraryValidationException.class, () -> new ExerciseLibrarySearchCriteria(null, 0, null, null, null, null));
        assertThrows(ExerciseLibraryValidationException.class, () -> new ExerciseLibrarySearchCriteria(null, 101, null, null, null, null));
        assertThrows(ExerciseLibraryValidationException.class, () -> new ExerciseLibrarySearchCriteria(null, null, -1, null, null, null));
        assertThrows(ExerciseLibraryValidationException.class, () -> new ExerciseLibrarySearchCriteria(null, null, null, 0, null, null));
        assertThrows(ExerciseLibraryValidationException.class, () -> new ExerciseLibrarySearchCriteria(null, null, null, null, 0, null));
        assertThrows(ExerciseLibraryValidationException.class, () -> new ExerciseLibrarySearchCriteria(null, null, null, null, null, 0));
    }

    @Test
    void listCriteriaRejectsInvalidPaging() {
        assertThrows(ExerciseLibraryValidationException.class, () -> new ExerciseLibraryListCriteria(0, null));
        assertThrows(ExerciseLibraryValidationException.class, () -> new ExerciseLibraryListCriteria(101, null));
        assertThrows(ExerciseLibraryValidationException.class, () -> new ExerciseLibraryListCriteria(null, -1));
    }

    @Test
    void repositoryMapsSearchAndReferenceResponsesFromWgerClient() {
        ExerciseLibrarySearchCriteria searchCriteria = new ExerciseLibrarySearchCriteria("push", 20, 0, 1, null, null);
        ExerciseLibraryListCriteria listCriteria = new ExerciseLibraryListCriteria(50, 0);
        ExerciseLibraryEntity libraryEntity = new ExerciseLibraryEntity(List.of(exerciseItemEntity()), 1, 10);
        ExerciseMuscleEntity muscleEntity = new ExerciseMuscleEntity(1, "Pectoralis", "Pecs", true, "main.svg", "secondary.svg");
        ExerciseReferenceEntity referenceEntity = new ExerciseReferenceEntity(2, "Barbell");

        when(wgerExerciseClient.searchExercises(searchCriteria)).thenReturn(libraryEntity);
        when(wgerExerciseClient.getExerciseById(10)).thenReturn(exerciseItemEntity());
        when(wgerExerciseClient.getMuscles(50, 0)).thenReturn(List.of(muscleEntity));
        when(wgerExerciseClient.getEquipment(50, 0)).thenReturn(List.of(referenceEntity));
        when(wgerExerciseClient.getCategories(50, 0)).thenReturn(List.of(referenceEntity));

        ExerciseLibrary searchResult = repository.searchExercises(searchCriteria);

        assertEquals(1, searchResult.count());
        assertEquals("Bench", repository.getExerciseById(10).name());
        assertEquals("Pecs", repository.getMuscles(listCriteria).get(0).nameEn());
        assertEquals("Barbell", repository.getEquipment(listCriteria).get(0).name());
        assertEquals("Barbell", repository.getCategories(listCriteria).get(0).name());

        verify(wgerExerciseClient).searchExercises(searchCriteria);
        verify(wgerExerciseClient).getExerciseById(10);
        verify(wgerExerciseClient).getMuscles(50, 0);
        verify(wgerExerciseClient).getEquipment(50, 0);
        verify(wgerExerciseClient).getCategories(50, 0);
    }

    private ExerciseLibraryItemEntity exerciseItemEntity() {
        return new ExerciseLibraryItemEntity(
                10,
                "uuid",
                "Bench",
                "desc",
                "source",
                null,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                null,
                "author"
        );
    }
}
