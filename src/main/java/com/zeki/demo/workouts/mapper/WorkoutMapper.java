package com.zeki.demo.workouts.mapper;

import com.zeki.demo.workouts.dto.CreateWorkoutRequest;
import com.zeki.demo.workouts.dto.UpdateWorkoutRequest;
import com.zeki.demo.workouts.dto.WorkoutResponse;
import com.zeki.demo.users.entity.User;
import com.zeki.demo.workouts.entity.Workout;
import org.springframework.stereotype.Component;

@Component
public class WorkoutMapper {

    public Workout toEntity(CreateWorkoutRequest request, User user) {
        return Workout.builder()
                .name(normalizeName(request.getName()))
                .workoutDate(request.getWorkoutDate())
                .durationMinutes(request.getDurationMinutes())
                .notes(normalizeNotes(request.getNotes()))
                .user(user)
                .build();
    }

    public void updateEntity(Workout workout, UpdateWorkoutRequest request) {
        workout.setName(normalizeName(request.getName()));
        workout.setWorkoutDate(request.getWorkoutDate());
        workout.setDurationMinutes(request.getDurationMinutes());
        workout.setNotes(normalizeNotes(request.getNotes()));
    }

    public WorkoutResponse toResponse(Workout workout) {
        return new WorkoutResponse(
                workout.getId(),
                workout.getName(),
                workout.getWorkoutDate(),
                workout.getDurationMinutes(),
                workout.getNotes(),
                null,
                null
        );
    }

    private String normalizeName(String name) {
        return name == null ? null : name.trim();
    }

    private String normalizeNotes(String notes) {
        if (notes == null) {
            return null;
        }

        String trimmedNotes = notes.trim();
        return trimmedNotes.isEmpty() ? null : trimmedNotes;
    }
}
