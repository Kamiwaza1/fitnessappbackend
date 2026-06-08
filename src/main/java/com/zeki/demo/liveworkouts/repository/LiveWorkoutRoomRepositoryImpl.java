package com.zeki.demo.liveworkouts.repository;

import com.zeki.demo.liveworkouts.domain.LiveWorkoutRoom;
import com.zeki.demo.liveworkouts.mapperEntityDomain.LiveWorkoutRoomEntityMapper;
import com.zeki.demo.liveworkouts.repository.jpa.LiveWorkoutRoomRepoJPA;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LiveWorkoutRoomRepositoryImpl implements LiveWorkoutRoomRepository {

    private static final String ACTIVE = "ACTIVE";
    private static final String CLOSED = "CLOSED";

    private final LiveWorkoutRoomRepoJPA liveWorkoutRoomRepoJPA;
    private final LiveWorkoutRoomEntityMapper liveWorkoutRoomEntityMapper;

    @Override
    public LiveWorkoutRoom saveActive(LiveWorkoutRoom room) {
        return save(room, ACTIVE, null);
    }

    @Override
    public LiveWorkoutRoom saveClosed(LiveWorkoutRoom room, Instant closedAt) {
        return save(room, CLOSED, closedAt);
    }

    private LiveWorkoutRoom save(LiveWorkoutRoom room, String status, Instant closedAt) {
        if (liveWorkoutRoomRepoJPA.existsById(room.roomCode())) {
            liveWorkoutRoomRepoJPA.deleteParticipantsByRoomCode(room.roomCode());
            liveWorkoutRoomRepoJPA.flush();
        }
        return liveWorkoutRoomEntityMapper.toDomain(
                liveWorkoutRoomRepoJPA.save(liveWorkoutRoomEntityMapper.toEntity(room, status, closedAt))
        );
    }

    @Override
    public Optional<LiveWorkoutRoom> findActiveRoom(String roomCode) {
        return liveWorkoutRoomRepoJPA.findByRoomCodeAndStatus(roomCode, ACTIVE)
                .map(liveWorkoutRoomEntityMapper::toDomain);
    }

    @Override
    public boolean exists(String roomCode) {
        return liveWorkoutRoomRepoJPA.existsById(roomCode);
    }
}
