package com.zeki.demo.exerciseLibrary.service;

import com.zeki.demo.exerciseLibrary.dto.ExerciseLibraryItemResponse;
import com.zeki.demo.exerciseLibrary.dto.ExerciseLibraryResponse;
import com.zeki.demo.exerciseLibrary.dto.ExerciseLibrarySearchRequest;
import com.zeki.demo.exerciseLibrary.dto.ExerciseMuscleResponse;
import com.zeki.demo.exerciseLibrary.dto.ExerciseReferenceResponse;
import java.util.List;

public interface ExerciseLibraryService {

    ExerciseLibraryResponse searchExercises(ExerciseLibrarySearchRequest request);

    ExerciseLibraryItemResponse getExerciseById(Integer exerciseId);

    List<ExerciseMuscleResponse> getMuscles(Integer limit, Integer offset);

    List<ExerciseReferenceResponse> getEquipment(Integer limit, Integer offset);

    List<ExerciseReferenceResponse> getCategories(Integer limit, Integer offset);
}
