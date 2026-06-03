package com.zeki.demo.liveworkouts.domain;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public record LiveWorkoutRoom(
        String roomCode,
        String hostEmail,
        UUID workoutId,
        String workoutName,
        Map<String, LiveWorkoutParticipant> participants,
        Instant createdAt,
        Instant lastActivityAt
) {
    public static LiveWorkoutRoom create(String roomCode, String hostEmail, UUID workoutId, String workoutName) {
        Instant now = Instant.now();
        Map<String, LiveWorkoutParticipant> participants = new LinkedHashMap<>();
        participants.put(hostEmail, LiveWorkoutParticipant.joined(hostEmail));
        return new LiveWorkoutRoom(roomCode, hostEmail, workoutId, workoutName, participants, now, now);
    }

    public LiveWorkoutRoom withParticipants(Map<String, LiveWorkoutParticipant> nextParticipants) {
        return new LiveWorkoutRoom(
                roomCode,
                hostEmail,
                workoutId,
                workoutName,
                new LinkedHashMap<>(nextParticipants),
                createdAt,
                Instant.now()
        );
    }
}
