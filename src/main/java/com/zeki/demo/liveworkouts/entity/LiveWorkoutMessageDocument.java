package com.zeki.demo.liveworkouts.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "live_workout_messages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveWorkoutMessageDocument {

    @Id
    private String messageId;

    @Indexed
    private String roomCode;

    private String senderEmail;
    private String senderName;
    private String text;

    @Indexed
    private Instant createdAt;
}
