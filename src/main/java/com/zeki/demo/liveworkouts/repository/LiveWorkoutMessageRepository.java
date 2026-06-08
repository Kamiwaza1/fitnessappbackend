package com.zeki.demo.liveworkouts.repository;

import com.zeki.demo.liveworkouts.domain.LiveWorkoutMessage;
import java.util.List;

public interface LiveWorkoutMessageRepository {

    LiveWorkoutMessage save(LiveWorkoutMessage message);

    List<LiveWorkoutMessage> findRecentByRoomCode(String roomCode, int limit);
}
