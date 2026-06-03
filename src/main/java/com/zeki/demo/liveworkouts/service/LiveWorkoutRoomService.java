package com.zeki.demo.liveworkouts.service;

import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.common.exception.NotFoundException;
import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutParticipant;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutRoom;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutProgressRequest;
import com.zeki.demo.liveworkouts.entity.LiveWorkoutParticipantValue;
import com.zeki.demo.liveworkouts.entity.LiveWorkoutRoomEntity;
import com.zeki.demo.liveworkouts.repository.LiveWorkoutRoomRepoJPA;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.service.WorkoutService;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LiveWorkoutRoomService {

    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 6;
    private static final String ACTIVE = "ACTIVE";
    private static final String CLOSED = "CLOSED";

    private final SecureRandom secureRandom = new SecureRandom();
    private final WorkoutService workoutService;
    private final LiveWorkoutRoomRepoJPA liveWorkoutRoomRepoJPA;

    @Transactional
    public LiveWorkoutRoom createRoom(UUID workoutId, String hostEmail) {
        Workout workout = workoutService.getWorkoutById(workoutId, hostEmail);
        String roomCode = generateUniqueRoomCode();
        LiveWorkoutRoom room = LiveWorkoutRoom.create(roomCode, hostEmail, workout.getWorkoutId(), workout.getWorkoutName());
        return toDomain(liveWorkoutRoomRepoJPA.save(toEntity(room, ACTIVE, null)));
    }

    @Transactional(readOnly = true)
    public LiveWorkoutRoom getRoom(String roomCode) {
        return toDomain(getActiveRoomEntity(roomCode));
    }

    @Transactional
    public LiveWorkoutRoom joinRoom(String roomCode, String userEmail) {
        return updateRoom(roomCode, room -> {
            Map<String, LiveWorkoutParticipant> participants = new LinkedHashMap<>(room.participants());
            participants.putIfAbsent(userEmail, LiveWorkoutParticipant.joined(userEmail));
            return room.withParticipants(participants);
        });
    }

    @Transactional
    public LiveWorkoutRoom leaveRoom(String roomCode, String userEmail) {
        return updateRoom(roomCode, room -> {
            Map<String, LiveWorkoutParticipant> participants = new LinkedHashMap<>(room.participants());
            participants.remove(userEmail);
            return room.withParticipants(participants);
        });
    }

    @Transactional
    public LiveWorkoutRoom updateProgress(String roomCode, String userEmail, LiveWorkoutProgressRequest request) {
        return updateRoom(roomCode, room -> {
            LiveWorkoutParticipant participant = room.participants().get(userEmail);
            if (participant == null) {
                throw new UnauthorizedException("Join the live workout before sending progress");
            }

            Map<String, LiveWorkoutParticipant> participants = new LinkedHashMap<>(room.participants());
            participants.put(userEmail, participant.withProgress(
                    request == null ? null : request.currentExerciseIndex(),
                    request == null ? null : request.completedSets(),
                    request == null ? null : request.status()
            ));
            return room.withParticipants(participants);
        });
    }

    @Transactional
    public LiveWorkoutRoom closeRoom(String roomCode, String userEmail) {
        LiveWorkoutRoomEntity entity = getActiveRoomEntity(roomCode);
        LiveWorkoutRoom room = toDomain(entity);
        if (!room.hostEmail().equals(userEmail)) {
            throw new UnauthorizedException("Only the host can close this live workout room");
        }
        liveWorkoutRoomRepoJPA.save(toEntity(room, CLOSED, Instant.now()));
        return room;
    }

    private LiveWorkoutRoom updateRoom(String roomCode, RoomUpdater updater) {
        LiveWorkoutRoom updated = updater.update(toDomain(getActiveRoomEntity(roomCode)));
        return toDomain(liveWorkoutRoomRepoJPA.save(toEntity(updated, ACTIVE, null)));
    }

    private String generateUniqueRoomCode() {
        for (int attempt = 0; attempt < 10; attempt++) {
            String roomCode = generateRoomCode();
            if (!liveWorkoutRoomRepoJPA.existsById(roomCode)) {
                return roomCode;
            }
        }
        throw new BadRequestException("Could not create live workout room code");
    }

    private String generateRoomCode() {
        StringBuilder builder = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            builder.append(CODE_CHARS.charAt(secureRandom.nextInt(CODE_CHARS.length())));
        }
        return builder.toString();
    }

    private String normalizeRoomCode(String roomCode) {
        if (roomCode == null || roomCode.isBlank()) {
            throw new BadRequestException("Room code is required");
        }
        return roomCode.trim().toUpperCase(Locale.ROOT);
    }

    private LiveWorkoutRoomEntity getActiveRoomEntity(String roomCode) {
        return liveWorkoutRoomRepoJPA.findByRoomCodeAndStatus(normalizeRoomCode(roomCode), ACTIVE)
                .orElseThrow(() -> new NotFoundException("Live workout room not found"));
    }

    private LiveWorkoutRoom toDomain(LiveWorkoutRoomEntity entity) {
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

    private LiveWorkoutRoomEntity toEntity(LiveWorkoutRoom room, String status, Instant closedAt) {
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

    private interface RoomUpdater {
        LiveWorkoutRoom update(LiveWorkoutRoom room);
    }
}
