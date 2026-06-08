package com.zeki.demo.exerciseLibrary.repository;

import com.zeki.demo.exerciseLibrary.domain.ExerciseLibrary;
import com.zeki.demo.exerciseLibrary.domain.ExerciseLibraryItem;
import com.zeki.demo.exerciseLibrary.domain.ExerciseLibraryListCriteria;
import com.zeki.demo.exerciseLibrary.domain.ExerciseLibrarySearchCriteria;
import com.zeki.demo.exerciseLibrary.domain.ExerciseMuscle;
import com.zeki.demo.exerciseLibrary.domain.ExerciseReference;
import java.util.List;

public interface ExerciseLibraryRepository {

    ExerciseLibrary searchExercises(ExerciseLibrarySearchCriteria criteria);

    ExerciseLibraryItem getExerciseById(Integer exerciseId);

    List<ExerciseMuscle> getMuscles(ExerciseLibraryListCriteria criteria);

    List<ExerciseReference> getEquipment(ExerciseLibraryListCriteria criteria);

    List<ExerciseReference> getCategories(ExerciseLibraryListCriteria criteria);
}
