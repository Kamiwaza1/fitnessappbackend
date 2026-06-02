package com.zeki.demo.liveworkouts.service;

import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.common.exception.NotFoundException;
import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutParticipant;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutRoom;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutProgressRequest;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.service.WorkoutService;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LiveWorkoutRoomService {

    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 6;

    private final Map<String, LiveWorkoutRoom> rooms = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();
    private final WorkoutService workoutService;

    public LiveWorkoutRoom createRoom(UUID workoutId, String hostEmail) {
        Workout workout = workoutService.getWorkoutById(workoutId, hostEmail);
        String roomCode = generateUniqueRoomCode();
        LiveWorkoutRoom room = LiveWorkoutRoom.create(roomCode, hostEmail, workout.getWorkoutId(), workout.getWorkoutName());
        rooms.put(roomCode, room);
        return room;
    }

    public LiveWorkoutRoom getRoom(String roomCode) {
        LiveWorkoutRoom room = rooms.get(normalizeRoomCode(roomCode));
        if (room == null) {
            throw new NotFoundException("Live workout room not found");
        }
        return room;
    }

    public LiveWorkoutRoom joinRoom(String roomCode, String userEmail) {
        return updateRoom(roomCode, room -> {
            Map<String, LiveWorkoutParticipant> participants = new LinkedHashMap<>(room.participants());
            participants.putIfAbsent(userEmail, LiveWorkoutParticipant.joined(userEmail));
            return room.withParticipants(participants);
        });
    }

    public LiveWorkoutRoom leaveRoom(String roomCode, String userEmail) {
        return updateRoom(roomCode, room -> {
            Map<String, LiveWorkoutParticipant> participants = new LinkedHashMap<>(room.participants());
            participants.remove(userEmail);
            return room.withParticipants(participants);
        });
    }

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

    public LiveWorkoutRoom closeRoom(String roomCode, String userEmail) {
        LiveWorkoutRoom room = getRoom(roomCode);
        if (!room.hostEmail().equals(userEmail)) {
            throw new UnauthorizedException("Only the host can close this live workout room");
        }
        rooms.remove(room.roomCode());
        return room;
    }

    private LiveWorkoutRoom updateRoom(String roomCode, RoomUpdater updater) {
        String normalizedRoomCode = normalizeRoomCode(roomCode);
        LiveWorkoutRoom updated = rooms.compute(normalizedRoomCode, (key, currentRoom) -> {
            if (currentRoom == null) {
                throw new NotFoundException("Live workout room not found");
            }
            return updater.update(currentRoom);
        });
        if (updated == null) {
            throw new NotFoundException("Live workout room not found");
        }
        return updated;
    }

    private String generateUniqueRoomCode() {
        for (int attempt = 0; attempt < 10; attempt++) {
            String roomCode = generateRoomCode();
            if (!rooms.containsKey(roomCode)) {
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

    private interface RoomUpdater {
        LiveWorkoutRoom update(LiveWorkoutRoom room);
    }
}
