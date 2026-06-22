package com.zeki.demo.liveworkouts.service;

import com.zeki.demo.common.exception.NotFoundException;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutParticipant;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutRoom;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutProgressRequest;
import com.zeki.demo.liveworkouts.domain.repository.LiveWorkoutRoomRepository;
import com.zeki.demo.liveworkouts.exception.LiveWorkoutAccessException;
import com.zeki.demo.liveworkouts.exception.LiveWorkoutValidationException;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.service.WorkoutService;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LiveWorkoutRoomServiceImpl implements LiveWorkoutRoomService {

    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 6;

    private final SecureRandom secureRandom = new SecureRandom();
    private final WorkoutService workoutService;
    private final LiveWorkoutRoomRepository liveWorkoutRoomRepository;

    @Override
    @Transactional
    public LiveWorkoutRoom createRoom(UUID workoutId, String hostEmail) {
        Workout workout = workoutService.getWorkoutById(workoutId, hostEmail);
        String roomCode = generateUniqueRoomCode();
        LiveWorkoutRoom room = LiveWorkoutRoom.create(roomCode, hostEmail, workout.getWorkoutId(), workout.getWorkoutName());
        return liveWorkoutRoomRepository.saveActive(room);
    }

    @Override
    @Transactional(readOnly = true)
    public LiveWorkoutRoom getRoom(String roomCode) {
        return getActiveRoom(roomCode);
    }

    @Override
    @Transactional
    public LiveWorkoutRoom joinRoom(String roomCode, String userEmail) {
        return updateRoom(roomCode, room -> {
            Map<String, LiveWorkoutParticipant> participants = new LinkedHashMap<>(room.participants());
            participants.putIfAbsent(userEmail, LiveWorkoutParticipant.joined(userEmail));
            return room.withParticipants(participants);
        });
    }

    @Override
    @Transactional
    public LiveWorkoutRoom leaveRoom(String roomCode, String userEmail) {
        return updateRoom(roomCode, room -> {
            Map<String, LiveWorkoutParticipant> participants = new LinkedHashMap<>(room.participants());
            participants.remove(userEmail);
            return room.withParticipants(participants);
        });
    }

    @Override
    @Transactional
    public LiveWorkoutRoom updateProgress(String roomCode, String userEmail, LiveWorkoutProgressRequest request) {
        return updateRoom(roomCode, room -> {
            LiveWorkoutParticipant participant = room.participants().get(userEmail);
            if (participant == null) {
                throw new LiveWorkoutAccessException("Join the live workout before sending progress");
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

    @Override
    @Transactional
    public LiveWorkoutRoom closeRoom(String roomCode, String userEmail) {
        LiveWorkoutRoom room = getActiveRoom(roomCode);
        if (!room.hostEmail().equals(userEmail)) {
            throw new LiveWorkoutAccessException("Only the host can close this live workout room");
        }
        liveWorkoutRoomRepository.saveClosed(room, Instant.now());
        return room;
    }

    private LiveWorkoutRoom updateRoom(String roomCode, RoomUpdater updater) {
        LiveWorkoutRoom updated = updater.update(getActiveRoom(roomCode));
        return liveWorkoutRoomRepository.saveActive(updated);
    }

    private LiveWorkoutRoom getActiveRoom(String roomCode) {
        return liveWorkoutRoomRepository.findActiveRoom(normalizeRoomCode(roomCode))
                .orElseThrow(() -> new NotFoundException("Live workout room not found"));
    }

    private String generateUniqueRoomCode() {
        for (int attempt = 0; attempt < 10; attempt++) {
            String roomCode = generateRoomCode();
            if (!liveWorkoutRoomRepository.exists(roomCode)) {
                return roomCode;
            }
        }
        throw new LiveWorkoutValidationException("Could not create live workout room code");
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
            throw new LiveWorkoutValidationException("Room code is required");
        }
        return roomCode.trim().toUpperCase(Locale.ROOT);
    }

    private interface RoomUpdater {
        LiveWorkoutRoom update(LiveWorkoutRoom room);
    }
}
