package com.zeki.demo.liveworkouts.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "email")
public class LiveWorkoutParticipantValue {

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "current_exercise_index", nullable = false)
    private Integer currentExerciseIndex;

    @Column(name = "completed_sets", nullable = false)
    private Integer completedSets;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;
}
