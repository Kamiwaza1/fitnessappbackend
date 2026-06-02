package com.zeki.demo.liveworkouts.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record LiveWorkoutRoomResponse(
        String roomCode,
        String hostEmail,
        UUID workoutId,
        String workoutName,
        List<LiveWorkoutParticipantResponse> participants,
        Instant createdAt,
        Instant lastActivityAt
) {
}
