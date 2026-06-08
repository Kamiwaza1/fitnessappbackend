package com.zeki.demo.liveworkouts.repository.mongo;

import com.zeki.demo.liveworkouts.entity.LiveWorkoutMessageDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LiveWorkoutMessageMongoRepository extends MongoRepository<LiveWorkoutMessageDocument, String> {

    List<LiveWorkoutMessageDocument> findTop100ByRoomCodeOrderByCreatedAtDesc(String roomCode);
}
