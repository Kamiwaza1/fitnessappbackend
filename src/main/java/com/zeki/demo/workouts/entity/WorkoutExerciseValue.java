package com.zeki.demo.workouts.entity;

import com.zeki.demo.workouts.enums.MuscleType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutExerciseValue {

    @Column(name = "exercise_name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_muscle_type", nullable = false)
    private MuscleType muscleType;

    @Column(name = "sets", nullable = false)
    private Integer sets;

    @Column(name = "reps", nullable = false)
    private Integer reps;

    @Column(name = "weight_kg", nullable = false)
    private Double weightKg;

    @Column(name = "notes")
    private String notes;
}
