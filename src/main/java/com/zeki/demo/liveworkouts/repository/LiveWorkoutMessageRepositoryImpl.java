package com.zeki.demo.liveworkouts.repository;

import com.zeki.demo.liveworkouts.domain.LiveWorkoutMessage;
import com.zeki.demo.liveworkouts.mapperEntityDomain.LiveWorkoutMessageEntityMapper;
import com.zeki.demo.liveworkouts.repository.mongo.LiveWorkoutMessageMongoRepository;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LiveWorkoutMessageRepositoryImpl implements LiveWorkoutMessageRepository {

    private final LiveWorkoutMessageMongoRepository liveWorkoutMessageMongoRepository;
    private final LiveWorkoutMessageEntityMapper liveWorkoutMessageEntityMapper;

    @Override
    public LiveWorkoutMessage save(LiveWorkoutMessage message) {
        return liveWorkoutMessageEntityMapper.toDomain(
                liveWorkoutMessageMongoRepository.save(liveWorkoutMessageEntityMapper.toEntity(message))
        );
    }

    @Override
    public List<LiveWorkoutMessage> findRecentByRoomCode(String roomCode, int limit) {
        return liveWorkoutMessageMongoRepository
                .findTop100ByRoomCodeOrderByCreatedAtDesc(roomCode)
                .stream()
                .limit(Math.max(1, Math.min(limit, 100)))
                .map(liveWorkoutMessageEntityMapper::toDomain)
                .sorted(Comparator.comparing(LiveWorkoutMessage::createdAt))
                .toList();
    }
}
