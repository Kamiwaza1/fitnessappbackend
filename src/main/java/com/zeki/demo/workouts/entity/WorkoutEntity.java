package com.zeki.demo.workouts.entity;

import com.zeki.demo.users.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;


import java.time.LocalDate;
import java.util.UUID;

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
    @Column(columnDefinition = "uuid")
    private UUID workoutId;


    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;


    @Column(name = "workout_date", nullable = false)
    private LocalDate workoutDate;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "notes")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;


    public void patch(String name, LocalDate workoutDate, Integer durationMinutes, String notes) {
        if (name != null) {
            this.name = name;
        }
        if (workoutDate != null) {
            this.workoutDate = workoutDate;
        }
        if (durationMinutes != null) {
            this.durationMinutes = durationMinutes;
        }
        if (notes != null) {
            this.notes = notes;
        }
    }
}
