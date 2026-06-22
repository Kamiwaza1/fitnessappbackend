package com.zeki.demo.liveworkouts.domain.repository;

import com.zeki.demo.liveworkouts.domain.LiveWorkoutRoom;
import java.time.Instant;
import java.util.Optional;

public interface LiveWorkoutRoomRepository {

    LiveWorkoutRoom saveActive(LiveWorkoutRoom room);

    LiveWorkoutRoom saveClosed(LiveWorkoutRoom room, Instant closedAt);

    Optional<LiveWorkoutRoom> findActiveRoom(String roomCode);

    boolean exists(String roomCode);
}
