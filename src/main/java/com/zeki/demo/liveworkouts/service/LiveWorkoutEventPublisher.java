package com.zeki.demo.liveworkouts.service;

import com.zeki.demo.liveworkouts.enums.LiveWorkoutEventType;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutEventResponse;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutMessageResponse;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LiveWorkoutEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publishRoomClosed(String roomCode, String actorEmail, LiveWorkoutRoomResponse room) {
        publish(roomCode, toEvent(LiveWorkoutEventType.ROOM_CLOSED, actorEmail, room));
    }

    public void publishUserJoined(String roomCode, String actorEmail, LiveWorkoutRoomResponse room) {
        publish(roomCode, toEvent(LiveWorkoutEventType.USER_JOINED, actorEmail, room));
    }

    public void publishUserLeft(String roomCode, String actorEmail, LiveWorkoutRoomResponse room) {
        publish(roomCode, toEvent(LiveWorkoutEventType.USER_LEFT, actorEmail, room));
    }

    public void publishProgressUpdated(String roomCode, String actorEmail, LiveWorkoutRoomResponse room) {
        publish(roomCode, toEvent(LiveWorkoutEventType.PROGRESS_UPDATED, actorEmail, room));
    }

    public void publishMessageSent(String roomCode, String actorEmail, LiveWorkoutMessageResponse message) {
        publish(roomCode, new LiveWorkoutEventResponse(LiveWorkoutEventType.MESSAGE_SENT, actorEmail, null, message));
    }

    private void publish(String roomCode, LiveWorkoutEventResponse event) {
        messagingTemplate.convertAndSend("/topic/live-workouts/" + roomCode.toUpperCase(), event);
    }

    private LiveWorkoutEventResponse toEvent(
            LiveWorkoutEventType type,
            String actorEmail,
            LiveWorkoutRoomResponse room
    ) {
        return new LiveWorkoutEventResponse(type, actorEmail, room);
    }
}
