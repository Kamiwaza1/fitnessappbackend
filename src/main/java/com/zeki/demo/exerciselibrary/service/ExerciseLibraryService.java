package com.zeki.demo.exerciselibrary.service;

import com.zeki.demo.exerciselibrary.domain.ExerciseLibrary;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibraryItem;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibraryListCriteria;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibrarySearchCriteria;
import com.zeki.demo.exerciselibrary.domain.ExerciseMuscle;
import com.zeki.demo.exerciselibrary.domain.ExerciseReference;
import java.util.List;

public interface ExerciseLibraryService {

    ExerciseLibrary searchExercises(ExerciseLibrarySearchCriteria criteria);

    ExerciseLibraryItem getExerciseById(Integer exerciseId);

    List<ExerciseMuscle> getMuscles(ExerciseLibraryListCriteria criteria);

    List<ExerciseReference> getEquipment(ExerciseLibraryListCriteria criteria);

    List<ExerciseReference> getCategories(ExerciseLibraryListCriteria criteria);
}
