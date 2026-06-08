package com.zeki.demo.liveworkouts.config;

import com.mongodb.client.model.Indexes;
import com.zeki.demo.liveworkouts.entity.LiveWorkoutMessageDocument;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class LiveWorkoutMongoInitializer {

    @Bean
    ApplicationRunner liveWorkoutMessageCollectionInitializer(MongoTemplate mongoTemplate) {
        return args -> {
            if (!mongoTemplate.collectionExists(LiveWorkoutMessageDocument.class)) {
                mongoTemplate.createCollection(LiveWorkoutMessageDocument.class);
            }

            mongoTemplate.getCollection(mongoTemplate.getCollectionName(LiveWorkoutMessageDocument.class))
                    .createIndex(Indexes.compoundIndex(
                            Indexes.ascending("roomCode"),
                            Indexes.descending("createdAt")
                    ));
        };
    }
}
