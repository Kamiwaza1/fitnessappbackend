package com.zeki.demo.workouts.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.EqualsAndHashCode;
import com.zeki.demo.users.entity.User;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "workouts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "workout_date", nullable = false)
    private LocalDate workoutDate;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void applyUpdate(String name, LocalDate workoutDate, Integer durationMinutes, String notes) {
        this.name = name;
        this.workoutDate = workoutDate;
        this.durationMinutes = durationMinutes;
        this.notes = notes;
    }
}
