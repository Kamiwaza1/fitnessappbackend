package com.zeki.demo.exerciselibrary.repository;

import com.zeki.demo.exerciselibrary.domain.repository.ExerciseLibraryRepository;
import com.zeki.demo.exerciselibrary.client.WgerExerciseClient;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibrary;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibraryItem;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibraryListCriteria;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibrarySearchCriteria;
import com.zeki.demo.exerciselibrary.domain.ExerciseMuscle;
import com.zeki.demo.exerciselibrary.domain.ExerciseReference;
import com.zeki.demo.exerciselibrary.mapperentitydomain.ExerciseLibraryEntityMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ExerciseLibraryRepositoryImpl implements ExerciseLibraryRepository {

    private final WgerExerciseClient wgerExerciseClient;
    private final ExerciseLibraryEntityMapper exerciseLibraryEntityMapper;

    @Override
    @Cacheable(
            cacheNames = "exercise-library:search",
            key = "{#criteria.q(), #criteria.limit(), #criteria.offset(), #criteria.muscleId(), #criteria.equipmentId(), #criteria.categoryId()}"
    )
    public ExerciseLibrary searchExercises(ExerciseLibrarySearchCriteria criteria) {
        return exerciseLibraryEntityMapper.toDomain(wgerExerciseClient.searchExercises(criteria));
    }

    @Override
    @Cacheable(cacheNames = "exercise-library:item", key = "#exerciseId")
    public ExerciseLibraryItem getExerciseById(Integer exerciseId) {
        return exerciseLibraryEntityMapper.toDomain(wgerExerciseClient.getExerciseById(exerciseId));
    }

    @Override
    @Cacheable(cacheNames = "exercise-library:muscles", key = "{#criteria.limit(), #criteria.offset()}")
    public List<ExerciseMuscle> getMuscles(ExerciseLibraryListCriteria criteria) {
        return exerciseLibraryEntityMapper.toMuscleDomains(wgerExerciseClient.getMuscles(criteria.limit(), criteria.offset()));
    }

    @Override
    @Cacheable(cacheNames = "exercise-library:equipment", key = "{#criteria.limit(), #criteria.offset()}")
    public List<ExerciseReference> getEquipment(ExerciseLibraryListCriteria criteria) {
        return exerciseLibraryEntityMapper.toReferenceDomains(wgerExerciseClient.getEquipment(criteria.limit(), criteria.offset()));
    }

    @Override
    @Cacheable(cacheNames = "exercise-library:categories", key = "{#criteria.limit(), #criteria.offset()}")
    public List<ExerciseReference> getCategories(ExerciseLibraryListCriteria criteria) {
        return exerciseLibraryEntityMapper.toReferenceDomains(wgerExerciseClient.getCategories(criteria.limit(), criteria.offset()));
    }
}
