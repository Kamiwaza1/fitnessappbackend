package com.zeki.demo.progress.repository;

import com.zeki.demo.workouts.domain.Workout;
import java.util.List;

public interface ProgressRepository {
    List<Workout> findAllByUserEmail(String userEmail);
}
