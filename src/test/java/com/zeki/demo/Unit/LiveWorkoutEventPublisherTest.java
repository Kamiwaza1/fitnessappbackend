package com.zeki.demo.Unit;

import com.zeki.demo.liveworkouts.domain.LiveWorkoutEventType;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutEventResponse;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutMessageResponse;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutRoomResponse;
import com.zeki.demo.liveworkouts.service.LiveWorkoutEventPublisher;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LiveWorkoutEventPublisherTest {

    @Test
    void publishUserJoinedBuildsEventAndSendsItToRoomTopic() {
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        LiveWorkoutEventPublisher publisher = new LiveWorkoutEventPublisher(messagingTemplate);
        LiveWorkoutRoomResponse room = roomResponse();
        ArgumentCaptor<LiveWorkoutEventResponse> eventCaptor = ArgumentCaptor.forClass(LiveWorkoutEventResponse.class);

        publisher.publishUserJoined("room1", "owner@example.com", room);

        verify(messagingTemplate).convertAndSend(eq("/topic/live-workouts/ROOM1"), eventCaptor.capture());
        LiveWorkoutEventResponse event = eventCaptor.getValue();
        assertEquals(LiveWorkoutEventType.USER_JOINED, event.type());
        assertEquals("owner@example.com", event.actorEmail());
        assertEquals(room, event.room());
    }

    @Test
    void publishMessageSentBuildsMessageEventAndSendsItToRoomTopic() {
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        LiveWorkoutEventPublisher publisher = new LiveWorkoutEventPublisher(messagingTemplate);
        LiveWorkoutMessageResponse message = new LiveWorkoutMessageResponse(
                "message-1",
                "ROOM1",
                "owner@example.com",
                "owner",
                "Nice set",
                Instant.now()
        );
        ArgumentCaptor<LiveWorkoutEventResponse> eventCaptor = ArgumentCaptor.forClass(LiveWorkoutEventResponse.class);

        publisher.publishMessageSent("room1", "owner@example.com", message);

        verify(messagingTemplate).convertAndSend(eq("/topic/live-workouts/ROOM1"), eventCaptor.capture());
        LiveWorkoutEventResponse event = eventCaptor.getValue();
        assertEquals(LiveWorkoutEventType.MESSAGE_SENT, event.type());
        assertEquals("owner@example.com", event.actorEmail());
        assertEquals(message, event.message());
    }

    private LiveWorkoutRoomResponse roomResponse() {
        return new LiveWorkoutRoomResponse(
                "ROOM1",
                "owner@example.com",
                UUID.randomUUID(),
                "Push Day",
                List.of(),
                Instant.now(),
                Instant.now()
        );
    }
}
