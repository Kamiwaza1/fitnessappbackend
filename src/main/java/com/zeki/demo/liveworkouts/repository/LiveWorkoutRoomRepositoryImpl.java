package com.zeki.demo.liveworkouts.repository;

import com.zeki.demo.liveworkouts.domain.repository.LiveWorkoutRoomRepository;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutRoom;
import com.zeki.demo.liveworkouts.entity.LiveWorkoutRoomEntity;
import com.zeki.demo.liveworkouts.mapperentitydomain.LiveWorkoutRoomEntityMapper;
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
        LiveWorkoutRoomEntity nextEntity = liveWorkoutRoomEntityMapper.toEntity(room, status, closedAt);
        LiveWorkoutRoomEntity savedEntity = liveWorkoutRoomRepoJPA.findById(room.roomCode())
                .map(existingEntity -> updateExistingRoom(existingEntity, nextEntity))
                .orElseGet(() -> liveWorkoutRoomRepoJPA.save(nextEntity));
        return liveWorkoutRoomEntityMapper.toDomain(savedEntity);
    }

    private LiveWorkoutRoomEntity updateExistingRoom(
            LiveWorkoutRoomEntity existingEntity,
            LiveWorkoutRoomEntity nextEntity
    ) {
        existingEntity.replaceWith(
                nextEntity.getHostEmail(),
                nextEntity.getWorkoutId(),
                nextEntity.getWorkoutName(),
                nextEntity.getStatus(),
                nextEntity.getCreatedAt(),
                nextEntity.getLastActivityAt(),
                nextEntity.getClosedAt(),
                nextEntity.getParticipants()
        );
        return liveWorkoutRoomRepoJPA.save(existingEntity);
    }

    @Override
    public Optional<LiveWorkoutRoom> findActiveRoom(String roomCode) {
        return liveWorkoutRoomRepoJPA.findByRoomCodeAndStatus(roomCode, ACTIVE)
                .map(liveWorkoutRoomEntityMapper::toDomain);
    }

    @Override
    public Optional<LiveWorkoutRoom> findActiveRoomForUpdate(String roomCode) {
        return liveWorkoutRoomRepoJPA.findByRoomCodeAndStatusForUpdate(roomCode, ACTIVE)
                .map(liveWorkoutRoomEntityMapper::toDomain);
    }

    @Override
    public boolean exists(String roomCode) {
        return liveWorkoutRoomRepoJPA.existsById(roomCode);
    }
}
