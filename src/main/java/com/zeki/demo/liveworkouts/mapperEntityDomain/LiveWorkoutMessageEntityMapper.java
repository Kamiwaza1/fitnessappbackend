package com.zeki.demo.liveworkouts.mapperEntityDomain;

import com.zeki.demo.liveworkouts.domain.LiveWorkoutMessage;
import com.zeki.demo.liveworkouts.entity.LiveWorkoutMessageDocument;
import org.springframework.stereotype.Component;

@Component
public class LiveWorkoutMessageEntityMapper {

    public LiveWorkoutMessageDocument toEntity(LiveWorkoutMessage message) {
        return LiveWorkoutMessageDocument.builder()
                .messageId(message.messageId())
                .roomCode(message.roomCode())
                .senderEmail(message.senderEmail())
                .senderName(message.senderName())
                .text(message.text())
                .createdAt(message.createdAt())
                .build();
    }

    public LiveWorkoutMessage toDomain(LiveWorkoutMessageDocument document) {
        return new LiveWorkoutMessage(
                document.getMessageId(),
                document.getRoomCode(),
                document.getSenderEmail(),
                document.getSenderName(),
                document.getText(),
                document.getCreatedAt()
        );
    }
}
