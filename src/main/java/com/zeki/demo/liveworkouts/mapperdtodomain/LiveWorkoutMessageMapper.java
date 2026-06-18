package com.zeki.demo.liveworkouts.mapperdtodomain;

import com.zeki.demo.liveworkouts.domain.LiveWorkoutMessage;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutMessageResponse;
import org.springframework.stereotype.Component;

@Component
public class LiveWorkoutMessageMapper {

    public LiveWorkoutMessageResponse toResponse(LiveWorkoutMessage message) {
        return new LiveWorkoutMessageResponse(
                message.messageId(),
                message.roomCode(),
                message.senderEmail(),
                message.senderName(),
                message.text(),
                message.createdAt()
        );
    }
}
