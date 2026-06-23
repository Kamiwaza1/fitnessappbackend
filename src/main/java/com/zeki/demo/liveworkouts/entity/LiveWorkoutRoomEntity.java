package com.zeki.demo.liveworkouts.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "live_workout_rooms")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "roomCode")
public class LiveWorkoutRoomEntity {

    @Id
    @Column(name = "room_code", length = 12)
    private String roomCode;

    @Column(name = "host_email", nullable = false)
    private String hostEmail;

    @Column(name = "workout_id", nullable = false, columnDefinition = "uuid")
    private UUID workoutId;

    @Column(name = "workout_name", nullable = false)
    private String workoutName;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "last_activity_at", nullable = false)
    private Instant lastActivityAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "live_workout_participants",
            joinColumns = @JoinColumn(name = "room_code", referencedColumnName = "room_code")
    )
    @OrderColumn(name = "participant_order")
    @Builder.Default
    private List<LiveWorkoutParticipantValue> participants = new ArrayList<>();

    public void replaceWith(
            String hostEmail,
            UUID workoutId,
            String workoutName,
            String status,
            Instant createdAt,
            Instant lastActivityAt,
            Instant closedAt,
            Collection<LiveWorkoutParticipantValue> nextParticipants
    ) {
        this.hostEmail = hostEmail;
        this.workoutId = workoutId;
        this.workoutName = workoutName;
        this.status = status;
        this.createdAt = createdAt;
        this.lastActivityAt = lastActivityAt;
        this.closedAt = closedAt;
        this.participants.clear();
        this.participants.addAll(nextParticipants);
    }
}
