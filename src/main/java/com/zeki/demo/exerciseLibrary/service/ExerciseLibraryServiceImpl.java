package com.zeki.demo.exerciseLibrary.service;

import com.zeki.demo.exerciseLibrary.client.WgerExerciseClient;
import com.zeki.demo.exerciseLibrary.dto.ExerciseLibraryItemResponse;
import com.zeki.demo.exerciseLibrary.dto.ExerciseLibraryResponse;
import com.zeki.demo.exerciseLibrary.dto.ExerciseLibrarySearchRequest;
import com.zeki.demo.exerciseLibrary.dto.ExerciseMuscleResponse;
import com.zeki.demo.exerciseLibrary.dto.ExerciseReferenceResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExerciseLibraryServiceImpl implements ExerciseLibraryService {

    private final WgerExerciseClient wgerExerciseClient;

    @Override
    public ExerciseLibraryResponse searchExercises(ExerciseLibrarySearchRequest request) {
        return wgerExerciseClient.searchExercises(request);
    }

    @Override
    public ExerciseLibraryItemResponse getExerciseById(Integer exerciseId) {
        return wgerExerciseClient.getExerciseById(exerciseId);
    }

    @Override
    public List<ExerciseMuscleResponse> getMuscles(Integer limit, Integer offset) {
        return wgerExerciseClient.getMuscles(limit, offset);
    }

    @Override
    public List<ExerciseReferenceResponse> getEquipment(Integer limit, Integer offset) {
        return wgerExerciseClient.getEquipment(limit, offset);
    }

    @Override
    public List<ExerciseReferenceResponse> getCategories(Integer limit, Integer offset) {
        return wgerExerciseClient.getCategories(limit, offset);
    }
}
