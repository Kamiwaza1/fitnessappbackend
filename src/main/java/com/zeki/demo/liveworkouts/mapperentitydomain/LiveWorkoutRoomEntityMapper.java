package com.zeki.demo.liveworkouts.mapperEntityDomain;

import com.zeki.demo.liveworkouts.domain.LiveWorkoutParticipant;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutRoom;
import com.zeki.demo.liveworkouts.entity.LiveWorkoutParticipantValue;
import com.zeki.demo.liveworkouts.entity.LiveWorkoutRoomEntity;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class LiveWorkoutRoomEntityMapper {

    public LiveWorkoutRoom toDomain(LiveWorkoutRoomEntity entity) {
        Map<String, LiveWorkoutParticipant> participants = new LinkedHashMap<>();
        entity.getParticipants().stream()
                .sorted(Comparator.comparing(LiveWorkoutParticipantValue::getJoinedAt))
                .forEach(participant -> participants.put(
                        participant.getEmail(),
                        new LiveWorkoutParticipant(
                                participant.getEmail(),
                                participant.getDisplayName(),
                                participant.getStatus(),
                                participant.getCurrentExerciseIndex(),
                                participant.getCompletedSets(),
                                participant.getJoinedAt()
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
                        .joinedAt(participant.joinedAt())
                        .build())
                .toList();
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
}
