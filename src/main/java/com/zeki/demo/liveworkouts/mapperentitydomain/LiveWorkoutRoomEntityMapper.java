package com.zeki.demo.liveworkouts.mapperentitydomain;

import com.zeki.demo.liveworkouts.domain.LiveWorkoutParticipant;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutRoom;
import com.zeki.demo.liveworkouts.entity.LiveWorkoutParticipantValue;
import com.zeki.demo.liveworkouts.entity.LiveWorkoutRoomEntity;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class LiveWorkoutRoomEntityMapper {

    public LiveWorkoutRoom toDomain(LiveWorkoutRoomEntity entity) {
        Map<String, LiveWorkoutParticipant> participants = new LinkedHashMap<>();
        entity.getParticipants().stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(
                        participant -> participantJoinedAt(participant, entity.getCreatedAt()),
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .forEach(participant -> participants.put(
                        participant.getEmail(),
                        new LiveWorkoutParticipant(
                                participant.getEmail(),
                                defaultDisplayName(participant),
                                defaultStatus(participant),
                                defaultNumber(participant.getCurrentExerciseIndex()),
                                defaultNumber(participant.getCompletedSets()),
                                participantJoinedAt(participant, entity.getCreatedAt())
                        )
                ));
        return new LiveWorkoutRoom(
                entity.getRoomCode(),
                entity.getHostEmail(),
                entity.getWorkoutId(),
                entity.getWorkoutName(),
                participants,
                entity.getCreatedAt(),
                entity.getLastActivityAt()
        );
    }

    public LiveWorkoutRoomEntity toEntity(LiveWorkoutRoom room, String status, Instant closedAt) {
        List<LiveWorkoutParticipantValue> participants = room.participants().values().stream()
                .map(participant -> LiveWorkoutParticipantValue.builder()
                        .email(participant.email())
                        .displayName(participant.displayName())
                        .status(participant.status())
                        .currentExerciseIndex(participant.currentExerciseIndex())
                        .completedSets(participant.completedSets())
                        .joinedAt(participant.joinedAt() == null ? room.createdAt() : participant.joinedAt())
                        .build())
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        return LiveWorkoutRoomEntity.builder()
                .roomCode(room.roomCode())
                .hostEmail(room.hostEmail())
                .workoutId(room.workoutId())
                .workoutName(room.workoutName())
                .status(status)
                .createdAt(room.createdAt())
                .lastActivityAt(room.lastActivityAt())
                .closedAt(closedAt)
                .participants(participants)
                .build();
    }

    private Instant participantJoinedAt(LiveWorkoutParticipantValue participant, Instant roomCreatedAt) {
        return participant.getJoinedAt() == null ? roomCreatedAt : participant.getJoinedAt();
    }

    private String defaultDisplayName(LiveWorkoutParticipantValue participant) {
        if (participant.getDisplayName() != null && !participant.getDisplayName().isBlank()) {
            return participant.getDisplayName();
        }
        String email = participant.getEmail();
        if (email == null || email.isBlank()) {
            return "User";
        }
        int atIndex = email.indexOf("@");
        return atIndex > 0 ? email.substring(0, atIndex) : email;
    }

    private String defaultStatus(LiveWorkoutParticipantValue participant) {
        return participant.getStatus() == null || participant.getStatus().isBlank()
                ? "ACTIVE"
                : participant.getStatus();
    }

    private Integer defaultNumber(Integer value) {
        return value == null ? 0 : value;
    }
}
