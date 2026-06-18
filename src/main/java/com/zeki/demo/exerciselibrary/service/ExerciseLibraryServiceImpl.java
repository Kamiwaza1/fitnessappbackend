package com.zeki.demo.exerciselibrary.service;

import com.zeki.demo.exerciselibrary.domain.ExerciseLibrary;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibraryItem;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibraryListCriteria;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibrarySearchCriteria;
import com.zeki.demo.exerciselibrary.domain.ExerciseMuscle;
import com.zeki.demo.exerciselibrary.domain.ExerciseReference;
import com.zeki.demo.exerciselibrary.repository.ExerciseLibraryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExerciseLibraryServiceImpl implements ExerciseLibraryService {

    private final ExerciseLibraryRepository exerciseLibraryRepository;

    @Override
    public ExerciseLibrary searchExercises(ExerciseLibrarySearchCriteria criteria) {
        return exerciseLibraryRepository.searchExercises(criteria);
    }

    @Override
    public ExerciseLibraryItem getExerciseById(Integer exerciseId) {
        return exerciseLibraryRepository.getExerciseById(exerciseId);
    }

    @Override
    public List<ExerciseMuscle> getMuscles(ExerciseLibraryListCriteria criteria) {
        return exerciseLibraryRepository.getMuscles(criteria);
    }

    @Override
    public List<ExerciseReference> getEquipment(ExerciseLibraryListCriteria criteria) {
        return exerciseLibraryRepository.getEquipment(criteria);
    }

    @Override
    public List<ExerciseReference> getCategories(ExerciseLibraryListCriteria criteria) {
        return exerciseLibraryRepository.getCategories(criteria);
    }
}
