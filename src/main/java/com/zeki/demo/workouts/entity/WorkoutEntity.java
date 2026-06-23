package com.zeki.demo.workouts.entity;

import com.zeki.demo.users.entity.UserEntity;
import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.enums.WorkoutDifficulty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workouts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "workoutId")
public class WorkoutEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "workout_id", columnDefinition = "uuid")
    private UUID workoutId;

    @Column(name = "name", nullable = false)
    private String workoutName;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private WorkoutCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "muscle_type", nullable = false)
    private MuscleType muscleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private WorkoutDifficulty difficulty;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "workout_date", nullable = false)
    private LocalDate workoutDate;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "sets", nullable = false)
    private Integer sets;

    @Column(name = "reps", nullable = false)
    private Integer reps;

    @Column(name = "weight_kg", nullable = false)
    private Double weightKg;

    @Column(name = "completed", nullable = false)
    private boolean completed;

    @Column(name = "notes")
    private String notes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "workout_exercises",
            joinColumns = @JoinColumn(name = "workout_id", referencedColumnName = "workout_id")
    )
    @OrderColumn(name = "exercise_order")
    @Builder.Default
    private List<WorkoutExerciseValue> exercises = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
