package com.zeki.demo.unit;

import com.zeki.demo.liveworkouts.exception.LiveWorkoutValidationException;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutMessage;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutParticipant;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutRoom;
import com.zeki.demo.liveworkouts.domain.repository.LiveWorkoutMessageRepository;
import com.zeki.demo.liveworkouts.service.LiveWorkoutMessageService;
import com.zeki.demo.liveworkouts.service.LiveWorkoutMessageServiceImpl;
import com.zeki.demo.liveworkouts.service.LiveWorkoutRoomService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

class LiveWorkoutMessageServiceTest {

    private static final String ROOM_CODE = "ROOM1";
    private static final String HOST_EMAIL = "host@example.com";
    private static final String GUEST_EMAIL = "guest@example.com";

    private LiveWorkoutRoomService liveWorkoutRoomService;
    private LiveWorkoutMessageRepository liveWorkoutMessageRepository;
    private LiveWorkoutMessageService liveWorkoutMessageService;

    @BeforeEach
    void setUp() {
        liveWorkoutRoomService = mock(LiveWorkoutRoomService.class);
        liveWorkoutMessageRepository = mock(LiveWorkoutMessageRepository.class);
        liveWorkoutMessageService = new LiveWorkoutMessageServiceImpl(
                liveWorkoutRoomService,
                liveWorkoutMessageRepository
        );
    }

    @Test
    void sendMessageRequiresParticipantAndStoresMessage() {
        LiveWorkoutRoom room = LiveWorkoutRoom.create(ROOM_CODE, HOST_EMAIL, UUID.randomUUID(), "Push");
        LiveWorkoutMessage saved = LiveWorkoutMessage.create(ROOM_CODE, HOST_EMAIL, "Nice set");

        when(liveWorkoutRoomService.joinRoom(ROOM_CODE, HOST_EMAIL)).thenReturn(room);
        when(liveWorkoutMessageRepository.save(any(LiveWorkoutMessage.class))).thenReturn(saved);

        LiveWorkoutMessage message = liveWorkoutMessageService.sendMessage(ROOM_CODE, HOST_EMAIL, "Nice set");

        assertEquals("Nice set", message.text());
        verify(liveWorkoutRoomService).joinRoom(ROOM_CODE, HOST_EMAIL);
        verify(liveWorkoutMessageRepository).save(any(LiveWorkoutMessage.class));
    }

    @Test
    void sendMessageJoinsSenderBeforeStoringMessage() {
        LiveWorkoutRoom room = LiveWorkoutRoom.create(ROOM_CODE, HOST_EMAIL, UUID.randomUUID(), "Push");
        LiveWorkoutRoom joinedRoom = room.withParticipants(Map.of(
                HOST_EMAIL, room.participants().get(HOST_EMAIL),
                GUEST_EMAIL, LiveWorkoutParticipant.joined(GUEST_EMAIL)
        ));
        LiveWorkoutMessage saved = LiveWorkoutMessage.create(ROOM_CODE, GUEST_EMAIL, "hello");

        when(liveWorkoutRoomService.joinRoom(ROOM_CODE, GUEST_EMAIL)).thenReturn(joinedRoom);
        when(liveWorkoutMessageRepository.save(any(LiveWorkoutMessage.class))).thenReturn(saved);

        LiveWorkoutMessage message = liveWorkoutMessageService.sendMessage(ROOM_CODE, GUEST_EMAIL, "  hello  ");

        assertEquals("hello", message.text());
        verify(liveWorkoutRoomService).joinRoom(ROOM_CODE, GUEST_EMAIL);
    }

    @Test
    void sendMessageRejectsBlankText() {
        assertThrows(
                LiveWorkoutValidationException.class,
                () -> liveWorkoutMessageService.sendMessage(ROOM_CODE, HOST_EMAIL, " ")
        );
    }

    @Test
    void getRecentMessagesAllowsAuthenticatedRoomViewers() {
        LiveWorkoutRoom room = LiveWorkoutRoom.create(ROOM_CODE, HOST_EMAIL, UUID.randomUUID(), "Push");
        LiveWorkoutMessage message = LiveWorkoutMessage.create(ROOM_CODE, HOST_EMAIL, "hello");

        when(liveWorkoutRoomService.getRoom(ROOM_CODE)).thenReturn(room);
        when(liveWorkoutMessageRepository.findRecentByRoomCode(ROOM_CODE, 100)).thenReturn(List.of(message));

        assertEquals(List.of(message), liveWorkoutMessageService.getRecentMessages(ROOM_CODE, HOST_EMAIL));
        assertEquals(List.of(message), liveWorkoutMessageService.getRecentMessages(ROOM_CODE, GUEST_EMAIL));
    }
}
