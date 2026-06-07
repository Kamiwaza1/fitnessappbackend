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
        return liveWorkoutRoomEntityMapper.toDomain(
                liveWorkoutRoomRepoJPA.save(liveWorkoutRoomEntityMapper.toEntity(room, ACTIVE, null))
        );
    }

    @Override
    public LiveWorkoutRoom saveClosed(LiveWorkoutRoom room, Instant closedAt) {
        return liveWorkoutRoomEntityMapper.toDomain(
                liveWorkoutRoomRepoJPA.save(liveWorkoutRoomEntityMapper.toEntity(room, CLOSED, closedAt))
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
