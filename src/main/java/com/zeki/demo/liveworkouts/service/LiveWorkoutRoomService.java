package com.zeki.demo.liveworkouts.service;

import com.zeki.demo.liveworkouts.domain.LiveWorkoutRoom;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutProgressRequest;
import java.util.UUID;

public interface LiveWorkoutRoomService {

    LiveWorkoutRoom createRoom(UUID workoutId, String hostEmail);

    LiveWorkoutRoom getRoom(String roomCode);

    LiveWorkoutRoom joinRoom(String roomCode, String userEmail);

    LiveWorkoutRoom leaveRoom(String roomCode, String userEmail);

    LiveWorkoutRoom updateProgress(String roomCode, String userEmail, LiveWorkoutProgressRequest request);

    LiveWorkoutRoom closeRoom(String roomCode, String userEmail);
}
