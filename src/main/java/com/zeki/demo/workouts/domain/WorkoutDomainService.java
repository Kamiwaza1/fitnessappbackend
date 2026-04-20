package com.zeki.demo.workouts.domain;

import com.zeki.demo.workouts.dto.CreateWorkoutRequest;
import com.zeki.demo.workouts.dto.UpdateWorkoutRequest;
import com.zeki.demo.workouts.entity.Workout;
import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.users.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class WorkoutDomainService {

    public Workout createWorkout(CreateWorkoutRequest request, User user) {
        if (request.getWorkoutDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Workout date cannot be in the past");
        }

        return Workout.builder()
                .name(normalizeName(request.getName()))
                .workoutDate(request.getWorkoutDate())
                .durationMinutes(request.getDurationMinutes())
                .notes(normalizeNotes(request.getNotes()))
                .user(user)
                .build();
    }

    public void updateWorkout(Workout workout, UpdateWorkoutRequest request) {
        if (workout.getWorkoutDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot update a workout in the past");
        }
        workout.applyUpdate(
                normalizeName(request.getName()),
                request.getWorkoutDate(),
                request.getDurationMinutes(),
                normalizeNotes(request.getNotes())
        );
    }

    public String buildDashboardData(List<Workout> workouts) {
        int totalWorkouts = workouts.size();
        String latestWorkoutDate = workouts.isEmpty() ? "N/A" : workouts.get(0).getWorkoutDate().toString();
        return "totalWorkouts=" + totalWorkouts + ", latestWorkoutDate=" + latestWorkoutDate;
    }

    private String normalizeName(String name) {
        return name == null ? null : name.trim();
    }

    private String normalizeNotes(String notes) {
        return notes == null ? null : notes.trim();
    }
}
